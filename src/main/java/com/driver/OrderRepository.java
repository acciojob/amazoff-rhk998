package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            if (partnerToOrderMap.containsKey(partnerId)){
                HashSet<String> orders = partnerToOrderMap.get(partnerId);
                orders.add(orderId);
            }else{
                HashSet<String> orders = new HashSet<>();
                orders.add(orderId);
                partnerToOrderMap.put(partnerId, orders);
            }

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);

            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order findOrderById(String orderId){
        return orderMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        return partnerMap.getOrDefault(partnerId, null);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        if(partnerToOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            return orders.size();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        if(partnerToOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            return new ArrayList<>(orders);
        }
        return new ArrayList<>();
    }

    public List<String> findAllOrders(){
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        if(partnerToOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.remove(partnerId);
            for(String orderId : orders){
                orderToPartnerMap.remove(orderId);
            }
        }

        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        if(orderToPartnerMap.containsKey(orderId)){
            String partnerId = orderToPartnerMap.remove(orderId);
            if(partnerToOrderMap.containsKey(partnerId)){
                HashSet<String> orders = partnerToOrderMap.get(partnerId);
                orders.remove(orderId);
            }
        }

        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders(){
        return orderMap.size() - orderToPartnerMap.size();
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
        if (!partnerToOrderMap.containsKey(partnerId)) {
            return null;
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
