package com.driver;

import java.util.*;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order

            if (partnerToOrderMap.containsKey(partnerId)){
                HashSet<String> orders = partnerToOrderMap.get(partnerId);
                orders.add(orderId);
                partnerToOrderMap.put(partnerId,orders);
            }else{
                HashSet<String> orders = new HashSet<>();
                orders.add(orderId);
                partnerToOrderMap.put(partnerId,orders);
            }

            DeliveryPartner partner = partnerMap.get(partnerId);
            int currentOrderCount = partner.getNumberOfOrders();
            partner.setNumberOfOrders(currentOrderCount + 1);

            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order findOrderById(String orderId){
        // your code here
//        return orderMap.get(orderId);
        return orderMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
//        return partnerMap.get(partnerId);
        return partnerMap.getOrDefault(partnerId, null);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        if(partnerToOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            return orders.size();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        HashSet<String> orders = new HashSet<>();
        if(partnerToOrderMap.containsKey(partnerId)){
            orders = partnerToOrderMap.get(partnerId);
        }
        List<String> orderList = new ArrayList<>(orders);
        return orderList;
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        List<String> ol  = new ArrayList<>();

        for(String oId : orderMap.keySet()){
            ol.add(oId);
        }

        return ol;
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if(partnerToOrderMap.containsKey(partnerId)){
            partnerToOrderMap.remove(partnerId);
        }

        for(String order : orderToPartnerMap.keySet()){
            if(orderToPartnerMap.get(order)==partnerId){
                orderToPartnerMap.remove(order);
            }
        }

        if(partnerMap.containsKey(partnerId)) partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID

        if(orderToPartnerMap.containsKey(orderId)) orderToPartnerMap.remove(orderId);

        for(String partner : partnerToOrderMap.keySet()){
            HashSet<String> ordrs = partnerToOrderMap.get(partner);
            if(ordrs.contains(orderId)) ordrs.remove(orderId);
            break;
        }

        if(orderMap.containsKey(orderId)) orderMap.remove(orderId);

    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        int totalOrders = orderMap.size();

        int partneredOrders = 0;
        for(String partner : partnerToOrderMap.keySet()){
            HashSet<String> orders = partnerToOrderMap.get(partner);
            partneredOrders += orders.size();
        }
        return totalOrders - partneredOrders;
    }

    private int convertTimeToMinutes(String deliveryTime) {
        String[] parts = deliveryTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
    private String convertMinutesToTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        int time = convertTimeToMinutes(timeString);

        int count = 0;

        if (!partnerToOrderMap.containsKey(partnerId)) {
            return count;
        }
        for (String orderId : partnerToOrderMap.get(partnerId)) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > time) {
                count++;
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if (!partnerToOrderMap.containsKey(partnerId)) {
            return null ;
        }
        int latestDeliveryTime = -1;
        for (String orderId : partnerToOrderMap.get(partnerId)) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > latestDeliveryTime) {
                latestDeliveryTime = order.getDeliveryTime();
            }
        }

        if (latestDeliveryTime == -1) {
            return null;
        }

        return convertMinutesToTime(latestDeliveryTime);
    }
}