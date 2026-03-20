package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MenuViewController {

    private final MenuService menuService;

    public MenuViewController(MenuService menuService) {
        this.menuService = menuService;
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

}