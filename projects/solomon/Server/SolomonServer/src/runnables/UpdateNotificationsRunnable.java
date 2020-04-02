package runnables;

import data.Notification;
import solomonserver.SolomonServer;

import java.sql.ResultSet;
import java.util.*;

public class UpdateNotificationsRunnable implements Runnable {
    private HashSet<Integer> notificationsSet;
    private HashMap<Integer, Queue<Notification>> notificationsMap;//key:userId value:notifications
    public UpdateNotificationsRunnable(HashMap<Integer, Queue<Notification>> notificationsMap) {
        notificationsSet = new HashSet<>();
        this.notificationsMap = notificationsMap;
    }
    @Override
    public void run() {
        while (true) {
            try {
                //get the users
                ArrayList<Integer> newUsers = new ArrayList<>();
                ResultSet usersResultSet = SolomonServer.getTableData("users");
                if(usersResultSet.isBeforeFirst()) {
                    while(usersResultSet.next()) {
                        int idUser = usersResultSet.getInt("idusers");
                        if(!notificationsMap.containsKey(idUser)) {//new user
                            notificationsMap.put(idUser, new LinkedList<Notification>());
                            newUsers.add(idUser);
                        }
                    }
                }
                usersResultSet.getStatement().close();

                ResultSet notificationsResultSet = SolomonServer.getTableData("notifications");
                if(notificationsResultSet.isBeforeFirst()) {
                    while (notificationsResultSet.next()) {
                        int idNotification = notificationsResultSet.getInt("idNotification");
                        String notificationType = notificationsResultSet.getString("type");
                        String message = notificationsResultSet.getString("message");
                        Notification notification = new Notification(idNotification, notificationType, message);
                        if(!notificationsSet.contains(idNotification)) { //add the notification to all the users
                            for(Map.Entry<Integer, Queue<Notification>> entry : notificationsMap.entrySet())
                                entry.getValue().add(notification);
                            notificationsSet.add(idNotification);
                        }
                        else { //add the notification only to the new users
                            for(Integer idUser : newUsers) {
                                notificationsMap.get(idUser).add(notification);
                            }
                        }
                    }
                }
                notificationsResultSet.getStatement().close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
