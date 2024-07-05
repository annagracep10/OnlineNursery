package com.techphantomexample.usermicroservice.webController;

import com.techphantomexample.usermicroservice.Dto.CombinedProductDTO;
import com.techphantomexample.usermicroservice.Dto.PlantDTO;
import com.techphantomexample.usermicroservice.Dto.PlanterDTO;
import com.techphantomexample.usermicroservice.Dto.SeedDTO;
import com.techphantomexample.usermicroservice.entity.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/user")
public class ProductWebController {

    private static final Logger log = LoggerFactory.getLogger(ProductWebController.class);
    private CombinedProductDTO combinedProduct;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${product.service.base-url}")
    public String productServiceBaseUrl;


    @GetMapping("/products")
    public String showProducts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        String url = productServiceBaseUrl;
        CombinedProductDTO combinedProduct = restTemplate.getForObject(url + "/products", CombinedProductDTO.class);
        model.addAttribute("combinedProduct", combinedProduct);
        model.addAttribute("user", user);
        return "product-list";
    }

    @GetMapping("/newProductForm")
    public String newProductForm(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);
        switch (category.toLowerCase()) {
            case "plant":
                model.addAttribute("plant", new PlantDTO());
                break;
            case "planter":
                model.addAttribute("planter", new PlanterDTO());
                break;
            case "seed":
                model.addAttribute("seed", new SeedDTO());
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }
        return "new_product";
    }

    @PostMapping("/createProduct")
    public String saveProduct(@RequestParam("category") String category, PlantDTO plant, PlanterDTO planter, SeedDTO seed, Model model) {
        String url = productServiceBaseUrl;
        try {
            switch (category.toLowerCase()) {
                case "plant":
                    model.addAttribute("plant", plant);
                    url += "/plant";
                    restTemplate.postForObject(url, plant, PlantDTO.class);
                    break;
                case "planter":
                    model.addAttribute("planter", planter);
                    url += "/planter";
                    restTemplate.postForObject(url, planter, PlanterDTO.class);
                    break;
                case "seed":
                    model.addAttribute("seed", seed);
                    url += "/seed";
                    restTemplate.postForObject(url, seed, SeedDTO.class);
                    break;
                default:
                    model.addAttribute("error", "Invalid product category");
                    return "new_product";
            }
            return "redirect:/user/products";
        } catch (Exception e) {
            model.addAttribute("category", category);
            model.addAttribute("error",e.getMessage());
            return  "new_product"; // or whatever view you want to return for errors
        }
    }

    @GetMapping("/showFormForUpdateProduct/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        String url = productServiceBaseUrl;
        switch (category.toLowerCase()) {
            case "plant":
                url += "/plant/" + id;
                PlantDTO plant = restTemplate.getForObject(url, PlantDTO.class);
                model.addAttribute("plant", plant);
                model.addAttribute("category", "plant");
                break;
            case "planter":
                url += "/planter/" + id;
                PlanterDTO planter = restTemplate.getForObject(url, PlanterDTO.class);
                model.addAttribute("planter", planter);
                model.addAttribute("category", "planter");
                break;
            case "seed":
                url += "/seed/" + id;
                SeedDTO seed = restTemplate.getForObject(url, SeedDTO.class);
                model.addAttribute("seed", seed);
                model.addAttribute("category", "seed");
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }
        return "update_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@RequestParam("category") String category, PlantDTO plant, PlanterDTO planter, SeedDTO seed, Model model) {
        String url = productServiceBaseUrl;
        try {
            switch (category.toLowerCase()) {
                case "plant":
                    url += "/plant/" + plant.getId();
                    restTemplate.put(url, plant);
                    break;
                case "planter":
                    url += "/planter/" + planter.getId();
                    restTemplate.put(url, planter);
                    break;
                case "seed":
                    url += "/seed/" + seed.getId();
                    restTemplate.put(url, seed);
                    break;
                default:
                    model.addAttribute("error", "Invalid product category");
                    return "update_product";
            }
            return "redirect:/user/products";
        } catch (Exception e) {
            model.addAttribute("category", category);
            model.addAttribute("error", e.getMessage());
            return "update_product";
        }
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        String url = productServiceBaseUrl;
        switch (category.toLowerCase()) {
            case "plant":
                url += "/plant/" + id;
                restTemplate.delete(url);
                break;
            case "planter":
                url += "/planter/" + id;
                restTemplate.delete(url);
                break;
            case "seed":
                url += "/seed/" + id;
                restTemplate.delete(url);
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "product_list";
        }
        return "redirect:/user/products";
    }


}
