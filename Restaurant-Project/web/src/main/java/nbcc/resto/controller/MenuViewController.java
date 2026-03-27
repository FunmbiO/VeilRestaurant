package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.resto.dto.CreateMenuItemRequest;
import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.dto.UpdateMenuRequest;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.service.MenuItemService;
import nbcc.resto.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String listMenus(Model model) {
        List<MenuDTO> menus = menuService.getAllMenus();
        model.addAttribute("menus", menus);
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