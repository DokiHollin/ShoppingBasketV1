package au.edu.sydney.soft3202.task1;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();

    private final AtomicLong counter = new AtomicLong();
    ShoppingBasket shoppingBasket = new ShoppingBasket();

    Map<String, String> sessions = new HashMap<>();

    String[] users = {"A", "B", "C", "D", "E"};

    Map<String,Customer> customers = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user) {

        // We are just checking the username, in the real world you would also check their password here
        // or authenticate the user some other way.
        if (!Arrays.asList(users).contains(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.\n");
        }

        if(customers.size() == 0){
            customers.put("A",new Customer("A"));
            customers.put("B",new Customer("B"));
            customers.put("C",new Customer("C"));
            customers.put("D",new Customer("D"));
            customers.put("E",new Customer("E"));
        }

        // Generate the session token.
        byte[] sessionTokenBytes = new byte[16];
        randomNumberGenerator.nextBytes(sessionTokenBytes);
        String sessionToken = hexFormatter.formatHex(sessionTokenBytes);

        // Store the association of the session token with the user.
        sessions.put(sessionToken, user);

        // Create HTTP headers including the instruction for the browser to store the session token in a cookie.
        String setCookieHeaderValue = String.format("session=%s; Path=/; HttpOnly; SameSite=Strict;", sessionToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", setCookieHeaderValue);

        // Redirect to the cart page, with the session-cookie-setting headers.
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();

    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken,
        Model model
    ) {
        if (!sessions.containsKey(sessionToken)) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }
        Customer customer = customers.get(sessions.get(sessionToken));
        model.addAttribute("customer", customer);
        model.addAttribute("basket", customer.getBasket());
        int[] array = new int[customer.getBasket().items.size()];
        model.addAttribute("emptyList", array);
        model.addAttribute("token", sessionToken);

        return "cart";
    }

    @PostMapping("/updateCart")
    public String updateCart(
                             @RequestParam(value = "emptyList",defaultValue = "") String list,
                             @RequestParam(value = "customerName",defaultValue = "") String name,
                             @CookieValue(value = "session", defaultValue = "") String sessionToken,
                             Model model ) {

        if(Objects.equals(list, "") || Objects.equals(name, "")){
            return "redirect:/cart";
        }
        String actualName = name.split(",")[0];
        String[] arrOfStr = list.split(",", customers.get(actualName).getBasket().items.size());
        int i = 0;

        for(Map.Entry<String,Integer> each: customers.get(actualName).getBasket().items.entrySet()){
            try{
                each.setValue(0);
                if(Integer.parseInt(arrOfStr[i]) > 0){
                    customers.get(actualName).getBasket().addItem(each.getKey(), Integer.parseInt(arrOfStr[i]));
                }

//                each.setValue(Integer.valueOf(arrOfStr[i]));
//                Integer a = Integer.valueOf(arrOfStr[i]);
                i++;
            }catch(Exception e){
                System.out.println(e);
                System.out.println("currently processing" + each.getKey());
                System.out.println(customers.get(actualName).getBasket().items.toString());
                System.out.println(customers.get(actualName).getBasket().values.toString());
                System.out.println(Arrays.toString(customers.get(actualName).getBasket().names));
                return "InvalidInput";
            }
        }


       //return ResponseEntity.status(HttpStatus.FOUND).body(name);
       return "redirect:/cart";
    }

//    @PostMapping("/newname")
//    public ResponseEntity<String> directAccessnewName(@RequestParam(value = "user", defaultValue = "") String user) {
//        if (!Arrays.asList(users).contains(user)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.\n");
//        }
//        return null;
//    }

    @PostMapping("/newname")
    public String newName(
            @CookieValue(value = "session", defaultValue = "") String sessionToken,
            @RequestParam(value = "name",defaultValue = "") String name,
            Model model
    ) {
        if (!sessions.containsKey(sessionToken)) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }
        model.addAttribute("customer", name);

        return "newname";
    }

    @PostMapping("/delname")
    public String del(@RequestParam(value = "name",defaultValue = "") String name,
                      @CookieValue(value = "session", defaultValue = "") String sessionToken,
                      Model model
    ) {
        if (!sessions.containsKey(sessionToken) || name.isEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }

        model.addAttribute("customer", customers.get(name));
        return "delname";
    }

    @PostMapping("/deleteItems")
    public String deleteItems(
            @RequestParam(value = "items",defaultValue = "") String items,
            @RequestParam(value = "userName",defaultValue = "") String userName) {

        String actualName = userName.split(",")[0];
        if(Objects.equals(items, "")){
            customers.get(actualName).basket.items = new HashMap<>();
            customers.get(actualName).basket.values = new HashMap<>();
            return "redirect:/cart";
        }
        // The arrOfstr contains the item which shouldn't delete
        String[] arrOfStr = items.split(",", customers.get(actualName).getBasket().items.size());
        ArrayList<String> shoudDelete = new ArrayList<>();
        for(Map.Entry<String,Integer> each: customers.get(actualName).basket.items.entrySet()){
            int found = 0;
            for(String eachName:arrOfStr){
                if(eachName.equals(each.getKey())){
                    found = 1;
                    break;
                }
            }
            if(found == 0){
                shoudDelete.add(each.getKey());
            }
        }
        for(String each: shoudDelete){
            customers.get(actualName).basket.addItem(each,1);
            customers.get(actualName).basket.removeItem(each,1);
            customers.get(actualName).basket.removeItem(each,1);
            customers.get(actualName).basket.items.remove(each);
            customers.get(actualName).basket.values.remove(each);
        }
        customers.get(actualName).basket.names = arrOfStr;


        //return ResponseEntity.status(HttpStatus.FOUND).body(Arrays.toString(arrOfStr));

        return "redirect:/cart";

    }

    @PostMapping("/adding")
    public String adding(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "newName") String name,
            @RequestParam(value = "cost") Object cost
    ){
        try{
            cost = Double.parseDouble(cost.toString());
        }catch(Exception e){
            return "InvalidPage";
        }
        name = name.toLowerCase();
        String actualName = userName.split(",")[0];
        ShoppingBasket basket = customers.get(actualName).basket;
        if(basket.items.containsKey(name)){
            return "redirect:/cart";
        }
        basket.addNewItem(name,(double) cost);
        basket.addNewItem(name,(double) cost);
//        basket.items.put(name,0);
//        basket.values.put(name, (double) cost);
//        basket.names = new String [basket.items.size()];
//        int i = 0;
//        for(String each: basket.items.keySet()){
//            basket.names[i] = each;
//            i++;
//        }

        return "redirect:/cart";
    }

    @PostMapping("/updatename")
    public String updateName(
            @CookieValue(value = "session", defaultValue = "") String sessionToken,
            @RequestParam(value = "name",defaultValue = "") String name,
            Model model
    ) {
        if (!sessions.containsKey(sessionToken) || name.isEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }
        model.addAttribute("customer", customers.get(name));

        return "updatename";
    }

    @PostMapping("/updateProcess")
    public String adding(
            @RequestParam(value = "itemName",defaultValue = "") String itemName,
            @RequestParam(value = "itemCost",defaultValue = "") String itemCost,
            @RequestParam(value = "userName",defaultValue = "") String userName
    ){

        String actualName = userName.split(",")[0];
        if(Objects.equals(itemName, "") || Objects.equals(itemCost, "")){
            customers.get(actualName).basket.items = new HashMap<>();
            customers.get(actualName).basket.values = new HashMap<>();
            return "redirect:/cart";
        }
        ShoppingBasket basket = customers.get(actualName).basket;
        String[] nameArray = itemName.split(",", customers.get(actualName).getBasket().items.size());
        String[] costArray = itemCost.split(",", customers.get(actualName).getBasket().items.size());
        ArrayList<Integer> amount = new ArrayList<>();


        for(Map.Entry<String,Integer> each: basket.items.entrySet()){
            amount.add(each.getValue());
        }
//        try{
//            basket.addItem(null,1);
//        }catch (Exception e){
//
//        }
//        try{
//            basket.addItem("123",1);
//        }catch (Exception e){
//
//        }
//        try{
//            basket.addItem(nameArray[0], -100);
//        }catch (Exception e){
//
//        }
//        try{
//            basket.addItem(nameArray[0], Integer.MAX_VALUE);
//            basket.addItem(nameArray[0], Integer.MAX_VALUE);
//        }catch (Exception e){
//
//        }

        basket.clear();
        basket.items.clear();
        basket.values.clear();
        basket.getValue();
        int i = 0;
        for(String each: nameArray){
            basket.items.put(each,amount.get(i)-1);
            i++;
        }
        int j = 0;
        for(String each: costArray){
            try{
                basket.values.put(nameArray[j], Double.valueOf(each));
                j++;
            }catch(Exception e){
                return "InvalidInput";
            }

        }

        System.out.println(basket.items.toString());
        basket.addItem(nameArray[0],1);
        System.out.println(Arrays.toString(basket.names));
        System.out.println(basket.values.toString());
        basket.names = nameArray;
        basket.getValue();










//        basket.clear();
//
//        int i = 0;
//        for(String each: nameArray){
//            if(amount.get(i) != 0) {
//                basket.addItem(each, amount.get(i));
//            }
//            i++;
//        }
//        basket.values.clear();
//        int j = 0;
//        for(String each: costArray){
//            try{
//                basket.values.put(nameArray[j], Double.valueOf(each));
//                j++;
//            }catch(Exception e){
//                return "InvalidInput";
//            }
//
//        }

        return "redirect:/cart";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session,
                         @RequestParam(value = "token",defaultValue = "") String token) {
        session.invalidate();
        sessions.remove(token);
        return "redirect:/login";
    }


    @GetMapping("/newname")
    public String redirectNewName(@CookieValue(value = "session", defaultValue = "") String sessionToken,
                                  @RequestParam(value = "name",defaultValue = "") String name,
                                  Model model){
        if (sessions.size() == 0 || name.isEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }else{
            for(String each: sessions.keySet()){
                name = each;
            }
            name = sessions.get(name);
            model.addAttribute("customer", customers.get(name));

            return "newname";
        }
    }

//    @GetMapping("/cart")
//    public String redirectCart(@CookieValue(value = "session", defaultValue = "") String sessionToken,
//                                  @RequestParam(value = "name",defaultValue = "") String name,
//                                  Model model){
//        if (!sessions.containsKey(sessionToken) && name.isEmpty()) {
//            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
//            return "InvalidPage";
//        }else{
//            model.addAttribute("customer", name);
//
//            return "cart";
//        }
//    }

    @GetMapping("/delname")
    public String redirectDelName(@CookieValue(value = "session", defaultValue = "") String sessionToken,
                                  @RequestParam(value = "name",defaultValue = "") String name,
                                  Model model){
        if (sessions.size() == 0 || name.isEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }else{
            for(String each: sessions.keySet()){
                name = each;
            }
            name = sessions.get(name);
            model.addAttribute("customer", customers.get(name));

            return "delname";
        }
    }

    @GetMapping("/updatename")
    public String redirectUpdateName(

                                  Model model){
        if (sessions.size() == 0) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised.\n");
            return "InvalidPage";
        }else{
            String name = "";
            for(String each: sessions.keySet()){
                name = each;
            }
            name = sessions.get(name);
            System.out.println(name);
            model.addAttribute("customer", customers.get(name));

            return "updatename";
        }
    }


//    @GetMapping("/counter")
//    public ResponseEntity<String> counter() {
//        counter.incrementAndGet();
//        return ResponseEntity.status(HttpStatus.OK).body("[" + counter + "]");
//    }
//
//    @GetMapping("/cost")
//    public ResponseEntity<String> cost() {
//        return ResponseEntity.status(HttpStatus.OK).body(
//            shoppingBasket.getValue() == null ? "0" : shoppingBasket.getValue().toString()
//        );
//    }
//
//    @GetMapping("/greeting")
//    public String greeting(
//        @RequestParam(name="name", required=false, defaultValue="World") String name,
//        Model model
//    ) {
//        model.addAttribute("name", name);
//        return "greeting";
//    }

}
