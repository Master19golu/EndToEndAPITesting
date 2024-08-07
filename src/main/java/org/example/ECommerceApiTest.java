package org.example;

import Pojo.LoginRequest;
import Pojo.LoginResponse;
import Pojo.OrderDetails;
import Pojo.Orders;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ECommerceApiTest {
    public static void main(String []args){
        //pojo class
        LoginRequest loreq= new LoginRequest();
        loreq.setUserEmail("anshika@gmail.com");
        loreq.setUserPassword("Iamking@000");
     RequestSpecification req =new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").setContentType(ContentType.JSON).build();
           RequestSpecification reqlogin =given().log().all().spec(req).body(loreq);
           //implementing deserialization
      LoginResponse loginResponse= reqlogin.when().post("/api/ecom/auth/login").then().log().all().extract().as(LoginResponse.class);
      String token= loginResponse.getToken();

      System.out.println(loginResponse.getToken());
      String UserId = loginResponse.getUserId();
      System.out.println(loginResponse.getUserId());


      //Add The Product
        RequestSpecification addProductBaseReq= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").
          addHeader("Authorization",token).build();
        RequestSpecification reqAddProduct=given().log().all().spec(addProductBaseReq).param("productName","Laptop")
                .param("productAddedBy ",UserId).param("productCategory","fashion")
                .param("productSubCategory","shirts").param("productPrice","11500")
                .param("productDescription","Lenova").param("productFor","women")
                .multiPart("productImage",new File("C://Users//umang//OneDrive//Desktop//productImage_1722249084058.jpg"));

                String addproductResponse=reqAddProduct.when().post("/api/ecom/product/add-product").then().log().all().extract().asString();
                JsonPath js = new JsonPath(addproductResponse);
                String productId=js.get("productId");
                System.out.println(productId);


                //create order
        RequestSpecification createOrderBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token). setContentType(ContentType.JSON).build();
        //adding order
        OrderDetails orderdetail = new OrderDetails();
        orderdetail.setCountry("india");
        orderdetail.setProductOrderedId(productId);
        //added order details
        List<OrderDetails> orderdetailsList = new ArrayList<OrderDetails>();
        orderdetailsList.add(orderdetail);

        Orders orders = new Orders();
        orders.setOrders(orderdetailsList);

        RequestSpecification createorder =given().log().all().spec(createOrderBaseReq).body(orders);
        String responseAddorder=createorder.when().post("/api/ecom/order/create-order").then().log().all().extract().asString();
        System.out.println(responseAddorder);

        //Delete Product
        RequestSpecification deleteprodbaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token). setContentType(ContentType.JSON).build();

        RequestSpecification deleteProdReq= given().log().all().spec(deleteprodbaseReq).pathParam("productId",productId);
        String deleteproductResponse= deleteProdReq.when().delete("/api/ecom/product/delete-product/{productId}").then().log().all().extract().asString();

      JsonPath js1 = new JsonPath(deleteproductResponse);
      String productId1 = js1.get("message");
        Assert.assertEquals("Product Deleted Successfully",productId1);
    }
}
