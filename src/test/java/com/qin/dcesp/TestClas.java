package com.qin.dcesp;

import java.util.*;


public class TestClas {

    public static void main(String[] args) {
        System.out.println(new TestClas().minimumRounds(new int[]{
                215,217,214,214,218,217,217,217,214,217,219,219,218,211,218,214,214,216,214,214,213,218,211,219,214,215,219,212,214,212,215,213,218,217,217,220,213,220,220,213,213,214,212,212,219,211,217,218,214,214,219,211,210,212,217,219,211,213,218,215,210,219,210,213,212,214,210,219,220,219,212,215,218,211,220,218,216,211,219,212,217,210,211,219,215,216,215,219,211,217,218,212,214,214,219,217,217,219,216,213,219,216,210,215,211,214,212,220,217,211,219,219,218,214,220,215,213,216,216,212,217,215,220,215,210,214,215,218,212,217,216,212,210,218,211,211,213,213,218,218,220,212,214,217,218,218,212,212,213,214,216,217,210,210,219,216,217,211,217,218,218,217,219,214,217,220,214,211,216,218,216,220,213,210,210,215,220,216,213,214,210,210,213,220,216,210,213,220,216,219,213,216,210,219,220,214,215,218,219,214
        }));
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
