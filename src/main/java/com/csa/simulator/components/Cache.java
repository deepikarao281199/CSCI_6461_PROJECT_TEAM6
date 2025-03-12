package com.csa.simulator.components;
/**Cache Class*/
public class Cache extends Transformer {
    public CacheData[] lines;
    public int first,last;

    public Cache(){
        this.first = this.last = 0;
        lines = new CacheData[16];
        for(int i=0;i<16;i++)
            lines[i] = new CacheData();
    }
    public void push(short key,short val){
        if(this.last == 16){
            for(int i=0;i<15;i++){
                lines[i].key=lines[i+1].key;
                lines[i].val=lines[i+1].val;
            }
            lines[15].key = key;
            lines[15].val = val;
            return ;
        }
        if(last==0){
            lines[last].key=key;
            lines[last].val=val;
            last++;
        }else{
            lines[last].key = key;
            lines[last].val = val;
            last++;
        }
    }
}
