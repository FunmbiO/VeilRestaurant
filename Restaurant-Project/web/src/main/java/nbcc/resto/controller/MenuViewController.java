package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.auth.security.AppUserDetails;
import nbcc.resto.dto.*;
import nbcc.resto.exception.MenuItemNotFoundException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.exception.UnauthorizedException;
import nbcc.resto.service.MenuItemService;
import nbcc.resto.service.MenuService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
public class MenuViewController {

    private final MenuService menuService;
    private final MenuItemService menuItemService;

    public MenuViewController(MenuService menuService, MenuItemService menuItemService) {
        this.menuService     = menuService;
        this.menuItemService = menuItemService;
    }

    // LIST  GET /menus
    @GetMapping("/menus")
    public String listMenus(@RequestParam(required = false) String search,
                            @AuthenticationPrincipal AppUserDetails currentUser,
                            Model model) {
        boolean searchActive = search != null && !search.isBlank();
        List<MenuDTO> menus = searchActive
                ? menuService.searchMenus(search)
                : menuService.getAllMenus();

        model.addAttribute("menus", menus);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("searchActive", searchActive);
        model.addAttribute("resultCount", menus.size());
        return "menus/list";
    }

    // DETAIL  GET /menus/{id}
    @GetMapping("/menus/{id}")
    public String menuDetail(@PathVariable Long id,
                             @AuthenticationPrincipal AppUserDetails currentUser,
                             Model model) {
        try {
            MenuDTO menu = menuService.getMenuById(id);

            boolean isAdmin   = currentUser != null && currentUser.isAdmin();
            boolean isCreator = currentUser != null
                    && Objects.equals(menu.getCreatedBy(), currentUser.getId());
            boolean canEdit    = isAdmin || isCreator;
            boolean canSuggest = currentUser != null && !isCreator;

            model.addAttribute("menu", menu);
            model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));
            model.addAttribute("canEdit", canEdit);
            model.addAttribute("canSuggest", canSuggest);
            return "menus/detail";

        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // CREATE FORM  GET /menus/new
    @GetMapping("/menus/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createMenuRequest", new CreateMenuRequest());
        return "menus/create";
    }

    // CREATE SUBMIT  POST /menus/new
    @PostMapping("/menus/new")
    public String submitCreateForm(
            @Valid @ModelAttribute CreateMenuRequest createMenuRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AppUserDetails currentUser,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "menus/create";
        }

        try {
            Long createdBy = currentUser != null ? currentUser.getId() : null;
            menuService.createMenu(createMenuRequest, createdBy);
            return "redirect:/menus?success=created";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "menus/create";
        }
    }

    // EDIT FORM  GET /menus/{id}/edit
    @GetMapping("/menus/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal AppUserDetails currentUser,
                               Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            menuService.assertCanModify(id, userId, isAdmin);

            MenuDTO menu = menuService.getMenuById(id);
            UpdateMenuRequest formRequest = new UpdateMenuRequest();
            formRequest.setName(menu.getName());
            formRequest.setDescription(menu.getDescription());

            model.addAttribute("menu", menu);
            model.addAttribute("formRequest", formRequest);
            model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));
            model.addAttribute("newMenuItem", new CreateMenuItemRequest());
            return "menus/edit";

        } catch (UnauthorizedException e) {
            return "redirect:/menus?error=unauthorized";
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // EDIT SUBMIT  POST /menus/{id}/edit
    @PostMapping("/menus/{id}/edit")
    public String submitEditForm(
            @PathVariable Long id,
            @Valid @ModelAttribute("formRequest") UpdateMenuRequest formRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AppUserDetails currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        Long userId = currentUser != null ? currentUser.getId() : null;

        if (bindingResult.hasErrors()) {
            try {
                MenuDTO menu = menuService.getMenuById(id);
                model.addAttribute("menu", menu);
                model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));
                model.addAttribute("newMenuItem", new CreateMenuItemRequest());
            } catch (MenuNotFoundException e) {
                return "redirect:/menus";
            }
            return "menus/edit";
        }

        try {
            menuService.updateMenu(id, formRequest, userId, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Menu updated successfully.");
            return "redirect:/menus/" + id;

        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You are not authorized to edit this menu.");
            return "redirect:/menus";
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            try {
                model.addAttribute("menu", menuService.getMenuById(id));
                model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));
                model.addAttribute("newMenuItem", new CreateMenuItemRequest());
            } catch (MenuNotFoundException notFound) {
                return "redirect:/menus";
            }
            return "menus/edit";
        }
    }

    // ADD MENU ITEM  POST /menus/{id}/items
    @PostMapping("/menus/{id}/items")
    public String addMenuItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("newMenuItem") CreateMenuItemRequest newMenuItem,
            BindingResult bindingResult,
            @AuthenticationPrincipal AppUserDetails currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        Long userId = currentUser != null ? currentUser.getId() : null;

        try {
            menuService.assertCanModify(id, userId, isAdmin);
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("itemError",
                    "You are not authorized to add items to this menu.");
            return "redirect:/menus/" + id;
        }

        if (bindingResult.hasErrors()) {
            try {
                MenuDTO menu = menuService.getMenuById(id);
                UpdateMenuRequest formRequest = new UpdateMenuRequest();
                formRequest.setName(menu.getName());
                formRequest.setDescription(menu.getDescription());
                model.addAttribute("menu", menu);
                model.addAttribute("formRequest", formRequest);
                model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));
                model.addAttribute("newMenuItem", newMenuItem);
                model.addAttribute("itemFormError", true);
            } catch (MenuNotFoundException e) {
                return "redirect:/menus";
            }
            return "menus/edit";
        }

        try {
            menuItemService.createMenuItem(id, newMenuItem);
            redirectAttributes.addFlashAttribute("itemSuccess", "Menu item added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("itemError", e.getMessage());
        }
        return "redirect:/menus/" + id + "/edit";
    }

    // EDIT ITEM FORM  GET /menus/{menuId}/items/{itemId}/edit
    @GetMapping("/menus/{menuId}/items/{itemId}/edit")
    public String showEditItemForm(@PathVariable Long menuId,
                                   @PathVariable Long itemId,
                                   @AuthenticationPrincipal AppUserDetails currentUser,
                                   Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            menuService.assertCanModify(menuId, userId, isAdmin);

            MenuDTO menu = menuService.getMenuById(menuId);
            MenuItemDTO item = menuItemService.getMenuItemById(itemId);
            UpdateMenuItemRequest formRequest = new UpdateMenuItemRequest();
            formRequest.setName(item.getName());
            formRequest.setDescription(item.getDescription());
            model.addAttribute("menu", menu);
            model.addAttribute("item", item);
            model.addAttribute("formRequest", formRequest);
            return "menus/edit-item";

        } catch (UnauthorizedException e) {
            return "redirect:/menus/" + menuId + "?error=unauthorized";
        } catch (MenuNotFoundException | MenuItemNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // EDIT ITEM SUBMIT  POST /menus/{menuId}/items/{itemId}/edit
    @PostMapping("/menus/{menuId}/items/{itemId}/edit")
    public String submitEditItemForm(
            @PathVariable Long menuId,
            @PathVariable Long itemId,
            @Valid @ModelAttribute("formRequest") UpdateMenuItemRequest formRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AppUserDetails currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        Long userId = currentUser != null ? currentUser.getId() : null;

        try {
            menuService.assertCanModify(menuId, userId, isAdmin);
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("itemError",
                    "You are not authorized to edit items on this menu.");
            return "redirect:/menus/" + menuId + "/edit";
        }

        if (bindingResult.hasErrors()) {
            try {
                model.addAttribute("menu", menuService.getMenuById(menuId));
                model.addAttribute("item", menuItemService.getMenuItemById(itemId));
            } catch (Exception e) {
                return "redirect:/menus";
            }
            return "menus/edit-item";
        }

        try {
            menuItemService.updateMenuItem(itemId, formRequest);
            redirectAttributes.addFlashAttribute("itemSuccess", "Menu item updated successfully.");
        } catch (MenuItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("itemError", "Menu item not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("itemError", e.getMessage());
        }
        return "redirect:/menus/" + menuId + "/edit";
    }

    // DELETE ITEM CONFIRM  GET /menus/{menuId}/items/{itemId}/delete
    @GetMapping("/menus/{menuId}/items/{itemId}/delete")
    public String showDeleteItemConfirm(@PathVariable Long menuId,
                                        @PathVariable Long itemId,
                                        @AuthenticationPrincipal AppUserDetails currentUser,
                                        Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            menuService.assertCanModify(menuId, userId, isAdmin);

            model.addAttribute("menu", menuService.getMenuById(menuId));
            model.addAttribute("item", menuItemService.getMenuItemById(itemId));
            return "menus/delete-item";

        } catch (UnauthorizedException e) {
            return "redirect:/menus/" + menuId + "?error=unauthorized";
        } catch (MenuNotFoundException | MenuItemNotFoundException e) {
            return "redirect:/menus/" + menuId + "/edit";
        }
    }

    // DELETE ITEM SUBMIT  POST /menus/{menuId}/items/{itemId}/delete
    @PostMapping("/menus/{menuId}/items/{itemId}/delete")
    public String confirmDeleteItem(@PathVariable Long menuId,
                                    @PathVariable Long itemId,
                                    @AuthenticationPrincipal AppUserDetails currentUser,
                                    RedirectAttributes redirectAttributes) {
        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        Long userId = currentUser != null ? currentUser.getId() : null;

        try {
            menuService.assertCanModify(menuId, userId, isAdmin);
            menuItemService.deleteMenuItem(itemId);
            redirectAttributes.addFlashAttribute("itemSuccess", "Menu item deleted successfully.");
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("itemError",
                    "You are not authorized to delete items on this menu.");
        } catch (MenuItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("itemError", "Menu item not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("itemError", e.getMessage());
        }
        return "redirect:/menus/" + menuId + "/edit";
    }

    // DELETE MENU CONFIRM  GET /menus/{id}/delete
    @GetMapping("/menus/{id}/delete")
    public String showDeleteConfirm(@PathVariable Long id,
                                    @AuthenticationPrincipal AppUserDetails currentUser,
                                    Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            menuService.assertCanModify(id, userId, isAdmin);

            MenuDTO menu = menuService.getMenuById(id);
            List<String> associatedEvents = menuService.getAssociatedEventNames(id);
            model.addAttribute("menu", menu);
            model.addAttribute("associatedEvents", associatedEvents);
            return "menus/delete";

        } catch (UnauthorizedException e) {
            return "redirect:/menus?error=unauthorized";
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // DELETE MENU SUBMIT  POST /menus/{id}/delete
    @PostMapping("/menus/{id}/delete")
    public String confirmDelete(@PathVariable Long id,
                                @AuthenticationPrincipal AppUserDetails currentUser,
                                RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            menuService.deleteMenu(id, userId, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Menu deleted successfully.");

        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You are not authorized to delete this menu.");
        } catch (MenuNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/menus";
    }
}