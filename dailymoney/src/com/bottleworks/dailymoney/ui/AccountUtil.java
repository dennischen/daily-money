package com.bottleworks.dailymoney.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;

public class AccountUtil {

    public static List<IndentNode> toIndentNode(List<Account> accl) {
        List<IndentNode> better = new ArrayList<IndentNode>();
        Map<String,IndentNode> tree = new LinkedHashMap<String,IndentNode>();
        for(Account acc:accl){
            String name = acc.getName();
            StringBuilder path = new StringBuilder();
            IndentNode node = null;
            String pp = null;
            String np = null;
            AccountType type = AccountType.find(acc.getType());
            int indent=0;
            for(String t:name.split("\\.")){
                if(t.length()==0){
                    continue;
                }
                pp = path.toString();
                if(path.length()!=0){
                    path.append(".");
                }
                np = path.append(t).toString();
                if((node = tree.get(np))!=null){
                    indent++;
                    continue;
                }
                node = new IndentNode(pp,t,indent,type,null);
                indent++;
                tree.put(np, node);
            }
            if(node!=null){
                node.account = acc;
            }
        }
        
        for(String key:tree.keySet()){
            IndentNode tn = tree.get(key);
            better.add(tn);
        }
        
        return better;
    }
    
    public static class IndentNode{
        private String path;
        private  String name;
        private  AccountType type;
        private  Account account;
        private  int indent;
        private String fullpath;
        
        public IndentNode(String path,String name,int indent,AccountType type,Account account){
            this.path = path;
            this.name = name;
            this.indent = indent;
            this.type = type;
            this.account = account;
            fullpath = (path==null||path.equals(""))?name:path+"."+name;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public AccountType getType() {
            return type;
        }

        public Account getAccount() {
            return account;
        }

        public int getIndent() {
            return indent;
        }
        
        public String getFullPath(){
            return fullpath;
        }
        
        
    }
}
