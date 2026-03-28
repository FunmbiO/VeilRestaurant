package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.resto.dto.*;
import nbcc.resto.exception.MenuItemNotFoundException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.service.MenuItemService;
import nbcc.resto.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MenuViewController {

    private final MenuService menuService;
    private final MenuItemService menuItemService;

    public MenuViewController(MenuService menuService, MenuItemService menuItemService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
    }

    // LIST  GET /menus
    @GetMapping("/menus")
    public String listMenus(@RequestParam(required = false) String search, Model model) {
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

    // MENU DETAIL  GET /menus/{id}
    @GetMapping("/menus/{id}")
    public String menuDetail(@PathVariable Long id, Model model) {
        try {
            MenuDTO menu = menuService.getMenuById(id);
            model.addAttribute("menu", menu);
            model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));  // add this
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
            Model model) {

        if (bindingResult.hasErrors()) {
            return "menus/create";
        }

        try {
            menuService.createMenu(createMenuRequest);
            return "redirect:/menus?success=created";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "menus/create";
        }
    }
    // Edit Form  GET /menus/{id}/edit

    @GetMapping("/menus/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            MenuDTO menu = menuService.getMenuById(id);
            UpdateMenuRequest formRequest = new UpdateMenuRequest();
            formRequest.setName(menu.getName());
            formRequest.setDescription(menu.getDescription());
            model.addAttribute("menu", menu);
            model.addAttribute("formRequest", formRequest);
            model.addAttribute("menuItems", menuItemService.getItemsByMenuId(id));  // add this
            model.addAttribute("newMenuItem", new CreateMenuItemRequest());          // add this
            return "menus/edit";
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // Edit Submit  POST /menus/{id}/edit

    @PostMapping("/menus/{id}/edit")
    public String submitEditForm(
            @PathVariable Long id,
            @Valid @ModelAttribute("formRequest") UpdateMenuRequest formRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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
            menuService.updateMenu(id, formRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Menu updated successfully.");
            return "redirect:/menus/" + id;
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            try {
                MenuDTO menu = menuService.getMenuById(id);
                model.addAttribute("menu", menu);
            } catch (MenuNotFoundException notFound) {
                return "redirect:/menus";
            }
            return "menus/edit";
        }
    }
    // Add Menu Item  POST /menus/{id}/items

    @PostMapping("/menus/{id}/items")
    public String addMenuItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("newMenuItem") CreateMenuItemRequest newMenuItem,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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
                model.addAttribute("itemFormError", true); // flag to scroll to item form
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

    // Edit Menu Item Form  GET /menus/{menuId}/items/{itemId}/edit

    @GetMapping("/menus/{menuId}/items/{itemId}/edit")
    public String showEditItemForm(@PathVariable Long menuId,
                                   @PathVariable Long itemId,
                                   Model model) {
        try {
            MenuDTO menu = menuService.getMenuById(menuId);
            MenuItemDTO item = menuItemService.getMenuItemById(itemId);
            UpdateMenuItemRequest formRequest = new UpdateMenuItemRequest();
            formRequest.setName(item.getName());
            formRequest.setDescription(item.getDescription());
            model.addAttribute("menu", menu);
            model.addAttribute("item", item);
            model.addAttribute("formRequest", formRequest);
            return "menus/edit-item";
        } catch (MenuNotFoundException | MenuItemNotFoundException e) {
            return "redirect:/menus";
        }
    }

    //  Edit Menu Item Submit  POST /menus/{menuId}/items/{itemId}/edit

    @PostMapping("/menus/{menuId}/items/{itemId}/edit")
    public String submitEditItemForm(
            @PathVariable Long menuId,
            @PathVariable Long itemId,
            @Valid @ModelAttribute("formRequest") UpdateMenuItemRequest formRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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

    // Delete Menu Item Confirm  GET /menus/{menuId}/items/{itemId}/delete

    @GetMapping("/menus/{menuId}/items/{itemId}/delete")
    public String showDeleteItemConfirm(@PathVariable Long menuId,
                                        @PathVariable Long itemId,
                                        Model model) {
        try {
            model.addAttribute("menu", menuService.getMenuById(menuId));
            model.addAttribute("item", menuItemService.getMenuItemById(itemId));
            return "menus/delete-item";
        } catch (MenuNotFoundException | MenuItemNotFoundException e) {
            return "redirect:/menus/" + menuId + "/edit";
        }
    }

    // Delete Menu Item Submit  POST /menus/{menuId}/items/{itemId}/delete

    @PostMapping("/menus/{menuId}/items/{itemId}/delete")
    public String confirmDeleteItem(@PathVariable Long menuId,
                                    @PathVariable Long itemId,
                                    RedirectAttributes redirectAttributes) {
        try {
            menuItemService.deleteMenuItem(itemId);
            redirectAttributes.addFlashAttribute("itemSuccess", "Menu item deleted successfully.");
        } catch (MenuItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("itemError", "Menu item not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("itemError", e.getMessage());
        }
        return "redirect:/menus/" + menuId + "/edit";
    }

    // Delete Confirm  GET /menus/{id}/delete

    @GetMapping("/menus/{id}/delete")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        try {
            MenuDTO menu = menuService.getMenuById(id);
            List<String> associatedEvents = menuService.getAssociatedEventNames(id);
            model.addAttribute("menu", menu);
            model.addAttribute("associatedEvents", associatedEvents);
            return "menus/delete";
        } catch (MenuNotFoundException e) {
            return "redirect:/menus";
        }
    }

    // Delete Submit  POST /menus/{id}/delete

    @PostMapping("/menus/{id}/delete")
    public String confirmDelete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            menuService.deleteMenu(id);
            redirectAttributes.addFlashAttribute("successMessage", "Menu deleted successfully.");
        } catch (MenuNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/menus";
    }
}