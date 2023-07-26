package au.edu.sydney.soft3202.task1;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingBasketTest {

    ShoppingBasket shoppingBasket;
    private ApplicationContext context;

    @BeforeEach
    public void setUp() {
        shoppingBasket = new ShoppingBasket();

        context = SpringApplication.run(ShoppingServiceApplication.class); // Literally just run our application.
    }
    @AfterEach // Need to stop the server or else port will remain in use next test
    public void serverStop() {
        SpringApplication.exit(context);

    }



    @Test
    public void getValueEmptyTest() {
        assertNull(shoppingBasket.getValue());
    }



    @Test
    public void myTest() {
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.ofString("123"))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            assertEquals(302, resp.statusCode());
            System.out.println(req);
            System.out.println(resp.toString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            fail();
        }
    }

    @Test
    public void notMatchedLogin(){
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/login"))
                    .POST(HttpRequest.BodyPublishers.ofString("123"))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            System.out.println(req);
            System.out.println(resp.toString());
            assertEquals(401, resp.statusCode());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            fail();
        }
    }

    @Test
    public void noSessionCodeCart(){

        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/cart?user=A"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            System.out.println(req);
            System.out.println(resp.toString());
            assertEquals(200, resp.statusCode());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            fail();
        }

    }

    @Test
    public void TestCart() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/cart"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .GET()
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("Cart response status code: " + response2.statusCode());
                System.out.println("Body is: " + response2.body());
                assertEquals(200, response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestCartUpdate() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateCart"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("customerName=A%2CA%2CA%2CA&emptyList=1%2C2%2C3%2C4"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestCartUpdate Cart response status code: " + response2.statusCode());
                System.out.println("Body is: " + response2.body());
                assertEquals(302, response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestCartUpdateEmpty() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateCart"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("customerName=\"\" emptyList=1%2C2%2C3%2C4"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                assertEquals(302, response2.statusCode());
                HttpRequest request23 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateCart"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("customerName=A%2CA%2CA%2CA&emptyList=G%2C2%2C3%2C4"))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void TestCartUpdateException() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateCart"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("customerName=A%2CA%2CA%2CA&emptyList=G%2C2%2C3%2C4"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("Exception status code is: "+response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void TestnewName() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/newname"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("name=Hollin"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestnewName status code is: "+response2.statusCode());
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noSessionCodenewName(){

        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/newname?user=A"))
                    .POST(HttpRequest.BodyPublishers.ofString("name=Hollin"))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertEquals(200, resp.statusCode());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            fail();
        }

    }


    @Test
    public void TestdelName() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/delname"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("name=A"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("delName status code is: "+response2.statusCode());
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noSessionCodedelName(){

        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/delname?user=A"))
                    .POST(HttpRequest.BodyPublishers.ofString("name=apple"))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertEquals(200, resp.statusCode());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            fail();
        }

    }

    @Test
    public void TestdelItem() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/deleteItems"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("items=apple%2Cpear&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestdelItem status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestdelEmptyItem() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/deleteItems"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("items=&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestdelEmptyItem status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void Testadding() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/adding"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("userName=A%2CA%2CA%2CA&newName=grape&cost=100"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("Testadding status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestaddingNotDouble() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/adding"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("userName=A%2CA%2CA%2CA&newName=grape&cost=A"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestaddingNotDouble status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestaddingDuplicate() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/adding"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("userName=A%2CA%2CA%2CA&newName=apple&cost=1"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestaddingDuplicate status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestUpdateName() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updatename"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("name=A"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateName status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestUpdateNameEmpty() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updatename"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("name="))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateNameEmpty status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestUpdateProcess() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=apple%2Capples%2Cappless%2Capplesss&itemCost=1%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateProcess status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/login"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("itemName=apple%2Capples%2Cappless%2Capplesss&itemCost=1%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                    .build();
            HttpResponse<String> response2 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestUpdateException() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=apple%2Capples%2Cappless%2Capplesss&itemCost=1%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateProcess status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void TestUpdateProcessNotDouble() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=apple%2Capples%2Cappless%2Capplesss&itemCost=A%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateProcessEmpty status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestUpdateProcessEmpty() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=&itemCost=1%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateProcessEmpty status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void loginGetmapping() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=&itemCost=1%2C2%2C3%2C4&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateProcessEmpty status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(302,response2.statusCode());
            }
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/login"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .GET()
                    .build();
            HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/logout"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .GET()
                    .build();
            HttpResponse<String> response4 = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void Testnewname() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/newname"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .GET()
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("Testnewname status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/newname?name=A"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestdelNameGet() {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/delname?name=A"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .GET()
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestdelNameGet status code is: "+response2.statusCode());
                System.out.println();
            }
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/delname?name="))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .GET()
                    .build();
            HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
            assertEquals(200,response3.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestupdateNameeGet() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/updatename"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .GET()
                .build();

        HttpClient client2 = HttpClient.newBuilder().build();

        HttpResponse<String> resp = client2.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updatename"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .GET()
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestdelNameGet status code is: "+response2.statusCode());
                System.out.println();
                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] arr = {"1","2"};

    }

    @Test
    public void itemSizeZero(){
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status code: " + response.statusCode());
            assertEquals(302, response.statusCode());
            if (response.statusCode() == 302) {
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/updateProcess"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("itemName=&itemCost=&userName=A%2CA%2CA%2CA"))
                        .build();

                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                System.out.println("TestUpdateName status code is: "+response2.statusCode());
                System.out.println();
//                assertEquals(200,response2.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void addItemEdgeTest(){
//        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager()).build();
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/login"))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(HttpRequest.BodyPublishers.ofString("user=A"))
//                .build();
//        assertThrows(IllegalArgumentException.class, ()->{
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Login response status code: " + response.statusCode());
//            assertEquals(302, response.statusCode());
//            if (response.statusCode() == 302) {
//                HttpRequest request2 = HttpRequest.newBuilder()
//                        .uri(URI.create("http://localhost:8080/updateCart"))
//                        .header("Content-Type", "application/x-www-form-urlencoded")
//                        .POST(HttpRequest.BodyPublishers.ofString("itemName=&itemCost=1%2C1%2C1%2C1&userName=A%2CA%2CA%2CA"))
//                        .build();
//
//                HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
//                System.out.println("TestUpdateName status code is: "+response2.statusCode());
//                System.out.println();
//            }
//        });
//
//    }

}


