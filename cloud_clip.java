import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

import java.util.regex.Matcher;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class cloud_clip {
    public static void main(String[] args) {
        new cloud_clip();
    }

    public static final String CLOUD_CLIP_URL = "https://script.google.com/macros/s/AKfycbwPmltPbWMMhVOepVeRPcoW8QbsvBEsBeg_9sdS5ChYfXQE9KM_ecqaVuI05f9z2avBcw/exec";

    public class get_clone implements Runnable {
        String password = null;
        String room_id = null;
        public get_clone(String password, String room_id) {
            this.password = password;
            this.room_id = room_id;
        }
        boolean running = false;
        public void shutdown() {
            running = false;
        }
        String value = "";
        @Override public synchronized void run() {
            running = true;
            while (running) {
                try {
                    URL obj = new URL(String.format(CLOUD_CLIP_URL + "?m=%s&p=%s&r=%s", "get", password, room_id));
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    
                    con.setRequestMethod("GET");
    
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
    
                    value = response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("通信に失敗しました。");
                }
            }
        }
        
    }
    public class cloud_clip_mode implements Runnable {
        String password = null;
        String room_id = null;
        public cloud_clip_mode(String password, String room_id) {
            this.password = password;
            this.room_id = room_id;
        }
        boolean running = false;
        public void shutdown() {
            running = false;
        }
        @Override public synchronized void run() {
            running = true;
            String last_clip = "";
            Toolkit kit = Toolkit.getDefaultToolkit();
	        Clipboard clip = kit.getSystemClipboard();
            get_clone get = new get_clone(password, room_id);
            new Thread(get) {{ start(); }};
            while (running) {
                try {
                    if (!last_clip.equals((String) clip.getData(DataFlavor.stringFlavor))) {
                        last_clip = (String) clip.getData(DataFlavor.stringFlavor);
                        System.out.println("SET : " + last_clip);
                        get.value = last_clip;
                        try {
                            URL obj = new URL(String.format(CLOUD_CLIP_URL + "?m=%s&p=%s&r=%s&v=%s", "set", password, room_id, SharpString.toSharp(last_clip)));
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
                            con.setRequestMethod("GET");
            
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();
            
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("通信に失敗しました。");
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                } catch (IOException e) { }
                if (!last_clip.equals(SharpString.toString(get.value))) {
                    last_clip = SharpString.toString(get.value);
                    System.out.println("GET : " + last_clip);
                    StringSelection ss = new StringSelection(last_clip);
		            clip.setContents(ss, ss);
                }
            }
        }
    }

    public cloud_clip() {
        boolean running = true;
        Scanner sc = new Scanner(System.in);
        while (running) {
            String[] input = sc.nextLine().split(" ");
            System.out.println(" -> ");
            if (input.length > 0) {
                switch (input[0]) {
                    case "room" : if (input.length > 1) {
                        switch (input[1]) {
                            case "create" :
                                if (input.length < 3) {
                                    System.out.println("room create <room password>");
                                    System.out.println("引数が足りません。");
                                    break;
                                }
                                System.out.println("部屋を作成しています。");
                                System.out.println("通信中..");
                                String room_id = null;
                                try {
                                    URL obj = new URL(String.format(CLOUD_CLIP_URL + "?m=%s&p=%s", "create", input[2]));
                                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                    con.setRequestMethod("GET");

                                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();

                                    room_id = response.toString();

                                    System.out.println("部屋を作成しました。");
                                    System.out.println("{\n  password : " + input[2] + "\n  room_id : " + room_id +"\n}");
                                    System.out.println("部屋に参加しますか？(y/n)");
                                    if (sc.nextLine().equals("y")) {
                                        System.out.println("部屋に参加しました");
                                        System.out.println("cloud_clip");
                                        System.out.println("{\n  password : " + input[2] + "\n  room_id : " + room_id +"\n}");
                                        System.out.println("退室する時は\"exit\"を入力します。");
                                        cloud_clip_mode cloud_clip = new cloud_clip_mode(input[2], room_id);
                                        new Thread(cloud_clip) {{
                                            start();
                                        }};
                                        while (true) {
                                            if (sc.nextLine().equals("exit")) break;
                                        }
                                        cloud_clip.shutdown();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("通信に失敗しました。");
                                }
                                break;
                            case "check" :
                                if (input.length < 4) {
                                    System.out.println("room check <room id> <room password>");
                                    System.out.println("引数が足りません。");
                                    break;
                                } 
                                Pattern number_pattern = Pattern.compile("^[0-9]+$");
                                Matcher matcher = number_pattern.matcher(input[2]);
                                if (!matcher.find()) {
                                    System.out.println("ルームIDには整数を入力してください。");
                                    break;
                                }
                                System.out.println("通信中..");
                                try {
                                    URL obj = new URL(String.format(CLOUD_CLIP_URL + "?m=%s&r=%s&p=%s", "check", input[2], input[3]));
                                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                    con.setRequestMethod("GET");

                                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();

                                    if (Boolean.parseBoolean(response.toString())) {
                                        System.out.println("ログインに成功しました。");
                                    } else {
                                        System.out.println("ログインに失敗しました。");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("通信に失敗しました。");
                                }
                                break;
                            case "help" :
                                System.out.println("room create <room password>");
                                System.out.println("  - 部屋を作成します。");
                                System.out.println("room check <room id> <room password>");
                                System.out.println("  - ログインテストができます。");
                                System.out.println("room enter <room id> <room password>");
                                System.out.println("  - ログインします。");
                                System.out.println("room help");
                                System.out.println("  - ヘルプを表示します。~ここに書いても意味ないよね~");
                                break;
                            case "enter" :
                                if (input.length < 4) {
                                    System.out.println("room enter <room id> <room password>");
                                    System.out.println("引数が足りません。");
                                    break;
                                } 
                                Pattern number_pattern2 = Pattern.compile("^[0-9]+$");
                                Matcher matcher2 = number_pattern2.matcher(input[2]);
                                if (!matcher2.find()) {
                                    System.out.println("ルームIDには整数を入力してください。");
                                    break;
                                }
                                System.out.println("通信中..");
                                try {
                                    URL obj = new URL(String.format(CLOUD_CLIP_URL + "?m=%s&r=%s&p=%s", "check", input[2], input[3]));
                                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                    con.setRequestMethod("GET");

                                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();

                                    if (Boolean.parseBoolean(response.toString())) {
                                        System.out.println("ログインに成功しました。");
                                    } else {
                                        System.out.println("ログインに失敗しました。");
                                        break;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("通信に失敗しました。");
                                    break;
                                }
                                System.out.println("部屋に参加しました");
                                System.out.println("cloud_clip");
                                System.out.println("{\n  password : " + input[3] + "\n  room_id : " + input[2] +"\n}");
                                System.out.println("退室する時は\"exit\"を入力します。");
                                cloud_clip_mode cloud_clip = new cloud_clip_mode(input[3], input[2]);
                                new Thread(cloud_clip) {{
                                    start();
                                }};
                                while (true) {
                                    if (sc.nextLine().equals("exit")) break;
                                }
                                cloud_clip.shutdown();
                                break;
                        }
                    } else {
                        System.out.println("room help でヘルプを表示できます。");
                    } break;
                    case "exit" :
                        running = false;
                        break;
                }
            } else {
                System.out.println("何か値を入力してください。\nコマンド一覧→help");
            } 
            System.out.println(" <-");
        }
        sc.close();
    }
}