package com.qin.dcesp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qin.dcesp.entity.GraphData;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class TestClas {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("g1");
        list.add("a2");
        Collections.sort(list);
        System.out.println(list);
    }


    public int minimumCardPickup(int[] cards) {
        int res = Integer.MAX_VALUE;
        Map<Integer,List<Integer>> map = new HashMap<>();
        for(int i = 0;i < cards.length;i++){
            if(map.containsKey(cards[i])){
                List<Integer> list = map.get(cards[i]);
                list.add(i);
                map.put(cards[i],list);
            }else{
                List<Integer> list = new ArrayList<>();
                list.add(i);
                map.put(cards[i],list);
            }
        }
        for(int key : map.keySet()){
            if(map.get(key).size() >= 2){
                res = Math.min(res,map.get(key).get(1) - map.get(key).get(0) + 1);
            }
        }
        return res == Integer.MAX_VALUE ? -1 : res;
    }


    public String removeDigit(String number, char digit) {
        int count = 0;
        List<Integer> index = new ArrayList<>();
        List<String> arr = new ArrayList<>();
        String res = "";
        for(int i = 0;i < number.length();i++){
            if(number.charAt(i) == digit){
                count++;
                index.add(i);
            }
        }
        if(count == 1){
            return number.substring(0,index.get(0)) + number.substring(index.get(0) + 1);
        }
        for(int i = 0;i < count;i++){
            int ind = index.get(i);
            if(ind == 0){
                arr.add(number.substring(ind + 1));
            }else if(ind + 1 <= number.length()){
                arr.add(number.substring(0,ind) + number.substring(ind + 1));
            }else if(ind == number.length() - 1){
                arr.add(number.substring(0,ind));
            }
        }
        for(int i = 0;i < arr.size();i++){
            if(i + 1 < arr.size()){
                res = findMax(arr.get(i),res);
            }
        }
        return res;
    }
    public String findMax(String a,String b){
        if("".equals(a)){
            return b;
        }
        if("".equals(b)){
            return a;
        }
        int i = 0;
        int j = 0;
        while(i < a.length() && j < b.length()){
            if(a.charAt(i) > b.charAt(j)){
                return a;
            }else if(a.charAt(i) < b.charAt(j)){
                return b;
            }else{
                i++;
                j++;
            }
        }
        return a;
    }

    public int binaryGap(int n) {
        String ns = Integer.toBinaryString(n);
        System.out.println(ns);
        int res = 0;
        char[] cs = ns.toCharArray();
        for(int i = 0;i < cs.length;i++){
            if(cs[i] == '1'){
                int count = 0;
                for(int j = i + 1;j < cs.length;j++){
                    if(j == '0'){
                        count++;
                    }else{
                        count++;
                        res = Math.max(count,res);
                        break;
                    }
                }
            }
        }
        return res;
    }


    public int minimumRounds(int[] tasks) {
        Map<Integer,Integer> map = new HashMap<>();
        for(int task : tasks){
            map.put(task,map.getOrDefault(task,0) + 1);
        }
        int res = 0;
        System.out.printf(map.toString());
        for(int key : map.keySet()){
            if(map.get(key) == 1){
                res = -1;
                break;
            }else{
                int nowCount = map.get(key);
                if(nowCount == 2 || nowCount == 3){
                    res += 1;
                }else if(nowCount % 3 == 0){
                    res += (nowCount / 3);
                }else if(nowCount % 2 == 0 && nowCount % 3 == 0){
                    res += (nowCount / 3);
                }else if(nowCount % 2 == 0 && nowCount % 3 == 1){
                    while(nowCount % 2 == 0 && nowCount % 3 == 1){
                        nowCount -= 2;
                        res++;
                    }
                    res += (nowCount / 3) + 1;
                }else if(nowCount % 2 == 0 && nowCount % 3 == 2){
                    res += (nowCount / 3) + 1;
                }else if(nowCount % 3 == 2){
                    res += (nowCount / 3) + 1;
                }else if(nowCount % 3 != 0 && nowCount % 2 != 0){
                    res += (2 * (nowCount / 5) + (nowCount % 5 == 0 ? 0 : 1));
                }

            }
        }
        return res;
    }

    public String digitSum(String s, int k) {
        if(s.length() <= k){
            return s;
        }
        int finalIndex = 0;
        List<String> list = new ArrayList<>();
        for(int i = 0;i + k < s.length();i += k){
            finalIndex = i;
            list.add(s.substring(i,i+k));
        }
        list.add(s.substring(finalIndex + k));
        StringBuilder res = new StringBuilder();
        for (String value : list) {
            res.append(String.valueOf(sumString(value)));
        }
        return digitSum(res.toString(),k);
    }
    private int sumString(String s){
        int res = 0;
        for(int i = 0;i < s.length();i++){
            res += (s.charAt(i) - '0');
        }
        return res;
    }




    public char nextGreatestLetter(char[] letters, char target) {
        char res = ' ';
        for(char c : letters){
            if((int)c > (int)target){
                res = c;
            }
        }
        if(res == ' '){
            return letters[0];
        }
        return res;
    }

    public int maximumCandies(int[] candies, long k) {
        long candieSum = 0;
        int res = 0;
        int mincandie = Integer.MAX_VALUE;
        for(int candie : candies){
            candieSum += candie;
            mincandie = Math.min(mincandie,candie);
        }
        if(k >= candieSum){
            return k > candieSum ? 0 : 1;
        }
        while(true){
            int mincandieCount = 0;
            for(int candie : candies){
                mincandieCount += (candie / mincandie);
            }
            if(mincandieCount > k){
                mincandie++;
            }else{
                res = mincandie;
                break;
            }
        }
        return res;
    }

    public boolean canReorderDoubled(int[] arr) {
        //意思就是说数组元素是否能构成满足后一个总是前一个元素的两倍这种结构
        Map<Integer,Integer> map = new HashMap<>();
        for(int num : arr){
            map.put(num,map.getOrDefault(num,0) + 1);
        }
        List<Integer> temp = new ArrayList<>();
        for(int key : map.keySet()){
            temp.add(key);
        }
        Collections.sort(temp,(a,b)->{
            int ta = a < 0 ? -a : a;
            int tb = b < 0 ? -b : b;
            return ta - tb;
        });
        for(int i = 0;i < temp.size();i++){
            int num = temp.get(i);
            if(map.isEmpty()){
                break;
            }
            if(!map.containsKey(num * 2) || map.get(num * 2) != map.get(num)){
                if(!map.containsKey(num)){
                    continue;
                }
                return false;
            }else{
                map.remove(num);
                map.remove(num * 2);
            }
        }
        return true;
    }


    public int networkBecomesIdle(int[][] edges, int[] patience) {
        int n = patience.length;
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; ++i) {
            adj[i] = new ArrayList<Integer>();
        }
        boolean[] visit = new boolean[n];
        for (int[] v : edges) {
            adj[v[0]].add(v[1]);
            adj[v[1]].add(v[0]);
        }

        Queue<Integer> queue = new ArrayDeque<Integer>();
        queue.offer(0);
        visit[0] = true;
        int dist = 1;
        int ans = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int curr = queue.poll();
                for (int v : adj[curr]) {
                    if (visit[v]) {
                        continue;
                    }
                    queue.offer(v);
                    int time = patience[v] * ((2 * dist - 1) / patience[v]) + 2 * dist + 1;
                    ans = Math.max(ans, time);
                    visit[v] = true;
                }
            }
            dist++;
        }
        return ans;
    }

}
