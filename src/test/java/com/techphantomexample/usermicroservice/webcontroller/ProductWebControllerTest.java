package com.techphantomexample.usermicroservice.webcontroller;

import com.techphantomexample.usermicroservice.dto.CombinedProductDTO;
import com.techphantomexample.usermicroservice.dto.PlantDTO;
import com.techphantomexample.usermicroservice.dto.PlanterDTO;
import com.techphantomexample.usermicroservice.dto.SeedDTO;
import com.techphantomexample.usermicroservice.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductWebControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ProductWebController productWebController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productWebController.productServiceBaseUrl = "http://localhost:9091/product";
    }


    @Test
    public void testShowProducts_UserNotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = productWebController.showProducts(session, model);

        assertEquals("redirect:/user/login", viewName);
        verify(restTemplate, never()).getForObject(anyString(), any(Class.class));
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    public void testShowProducts() {
        User user = new User();
        CombinedProductDTO combinedProduct = new CombinedProductDTO();
        when(session.getAttribute("user")).thenReturn(user);
        when(restTemplate.getForObject(anyString(), eq(CombinedProductDTO.class))).thenReturn(combinedProduct);

        String viewName = productWebController.showProducts(session, model);

        assertEquals("product-list", viewName);
        verify(model, times(1)).addAttribute("combinedProduct", combinedProduct);
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testNewProductForm_Plant() {

        String viewName = productWebController.newProductForm("plant", model);

        assertEquals("new_product", viewName);

        verify(model, times(1)).addAttribute("category", "plant");
        verify(model, times(1)).addAttribute(eq("plant"), any(PlantDTO.class));
    }

    @Test
    public void testNewProductForm_Planter() {

        String viewName = productWebController.newProductForm("planter", model);

        assertEquals("new_product", viewName);
        verify(model, times(1)).addAttribute("category", "planter");
        verify(model, times(1)).addAttribute(eq("planter"), any(PlanterDTO.class));
    }

    @Test
    public void testNewProductForm_Seed() {

        String viewName = productWebController.newProductForm("seed", model);

        assertEquals("new_product", viewName);
        verify(model, times(1)).addAttribute("category", "seed");
        verify(model, times(1)).addAttribute(eq("seed"), any(SeedDTO.class));
    }

    @Test
    public void testNewProductForm_InvalidCategory() {

        String viewName = productWebController.newProductForm("invalid", model);

        assertEquals("new_product", viewName);
        verify(model, times(1)).addAttribute("category", "invalid");
        verify(model, times(1)).addAttribute("error", "Invalid product category");
    }

    @Test
    public void testSaveProduct_Plant() {
        PlantDTO plant = new PlantDTO();

        String viewName = productWebController.saveProduct("plant", plant, null, null, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:9091/product/plant"), eq(plant), eq(PlantDTO.class));
    }

    @Test
    public void testSaveProduct_Planter() {
        PlanterDTO planter = new PlanterDTO();

        String viewName = productWebController.saveProduct("planter", null, planter, null, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:9091/product/planter"), eq(planter), eq(PlanterDTO.class));
    }

    @Test
    public void testSaveProduct_Seed() {
        SeedDTO seed = new SeedDTO();

        String viewName = productWebController.saveProduct("seed", null, null, seed, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:9091/product/seed"), eq(seed), eq(SeedDTO.class));
    }

    @Test
    public void testSaveProduct_InvalidCategory() {
        String category = "invalid";

        String viewName = productWebController.saveProduct(category, null, null, null, model);

        assertEquals("new_product", viewName);
        verify(model).addAttribute("error", "Invalid product category");
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    public void testSaveProduct_ExceptionHandling() {
        String category = "plant";
        PlantDTO plant = new PlantDTO();
        when(restTemplate.postForObject(anyString(), eq(plant), eq(PlantDTO.class))).thenThrow(new RuntimeException("Test exception"));

        String viewName = productWebController.saveProduct(category, plant, null, null, model);

        assertEquals("new_product", viewName);
        verify(model).addAttribute("category", category);
        verify(model).addAttribute("error", "Test exception");
    }

    @Test
    public void testShowFormForUpdate_Plant() {
        PlantDTO plant = new PlantDTO();
        when(restTemplate.getForObject(eq("http://localhost:9091/product/plant/1"), eq(PlantDTO.class))).thenReturn(plant);

        String viewName = productWebController.showFormForUpdate(1L, "plant", model);

        assertEquals("update_product", viewName);
        verify(model, times(1)).addAttribute("plant", plant);
        verify(model, times(1)).addAttribute("category", "plant");
    }

    @Test
    public void testShowFormForUpdate_Planter() {
        PlanterDTO planter = new PlanterDTO();
        when(restTemplate.getForObject(eq("http://localhost:9091/product/planter/1"), eq(PlanterDTO.class))).thenReturn(planter);

        String viewName = productWebController.showFormForUpdate(1L, "planter", model);

        assertEquals("update_product", viewName);
        verify(model, times(1)).addAttribute("planter", planter);
        verify(model, times(1)).addAttribute("category", "planter");
    }

    @Test
    public void testShowFormForUpdate_Seed() {
        SeedDTO seed = new SeedDTO();
        when(restTemplate.getForObject(eq("http://localhost:9091/product/seed/1"), eq(SeedDTO.class))).thenReturn(seed);

        String viewName = productWebController.showFormForUpdate(1L, "seed", model);

        assertEquals("update_product", viewName);
        verify(model, times(1)).addAttribute("seed", seed);
        verify(model, times(1)).addAttribute("category", "seed");
    }

    @Test
    public void testShowFormForUpdate_InvalidCategory() {

        String viewName = productWebController.showFormForUpdate(1L, "invalid", model);

        assertEquals("new_product", viewName);
        verify(model, times(1)).addAttribute("error", "Invalid product category");
    }

    @Test
    public void testUpdateProduct_Plant() {
        PlantDTO plant = new PlantDTO();
        plant.setId(1);

        String viewName = productWebController.updateProduct("plant", plant, null, null, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).put(eq("http://localhost:9091/product/plant/1"), eq(plant));
    }

    @Test
    public void testUpdateProduct_Planter() {
        PlanterDTO planter = new PlanterDTO();
        planter.setId(1);

        String viewName = productWebController.updateProduct("planter", null, planter, null, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).put(eq("http://localhost:9091/product/planter/1"), eq(planter));
    }

    @Test
    public void testUpdateProduct_Seed() {
        SeedDTO seed = new SeedDTO();
        seed.setId(1);

        String viewName = productWebController.updateProduct("seed", null, null, seed, model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).put(eq("http://localhost:9091/product/seed/1"), eq(seed));
    }

    @Test
    public void testUpdateProduct_InvalidCategory() {

        String viewName = productWebController.updateProduct("invalid", null, null, null, model);

        assertEquals("update_product", viewName);
        verify(model, times(1)).addAttribute("error", "Invalid product category");
    }

    @Test
    public void testUpdateProduct_ExceptionHandling() {
        String category = "plant";
        PlantDTO plant = new PlantDTO();
        plant.setId(1);
        doThrow(new RuntimeException("Test exception")).when(restTemplate).put(anyString(), eq(plant));

        String viewName = productWebController.updateProduct(category, plant, null, null, model);

        assertEquals("update_product", viewName);
        verify(model).addAttribute("category", category);
        verify(model).addAttribute("error", "Test exception");
    }

    @Test
    public void testDeleteProduct_Plant() {

        String viewName = productWebController.deleteProduct(1L, "plant", model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).delete(eq("http://localhost:9091/product/plant/1"));
    }

    @Test
    public void testDeleteProduct_Planter() {

        String viewName = productWebController.deleteProduct(1L, "planter", model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).delete(eq("http://localhost:9091/product/planter/1"));
    }

    @Test
    public void testDeleteProduct_Seed() {

        String viewName = productWebController.deleteProduct(1L, "seed", model);

        assertEquals("redirect:/user/products", viewName);
        verify(restTemplate, times(1)).delete(eq("http://localhost:9091/product/seed/1"));
    }

    @Test
    public void testDeleteProduct_InvalidCategory() {

        String viewName = productWebController.deleteProduct(1L, "invalid", model);

        assertEquals("product_list", viewName);
        verify(model, times(1)).addAttribute("error", "Invalid product category");
    }

}
