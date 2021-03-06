package pt.uminho.sdc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class ControllerImpl implements Controller {

    private static Logger logger = LoggerFactory.getLogger(ControllerImpl.class);

    
    Map<String, Integer> linha1 = new HashMap<String, Integer>();
    Map<String, Integer> linha2 = new HashMap<String, Integer>();
    Map<String, Integer> linha3 = new HashMap<String, Integer>();

    public ControllerImpl(){
        linha1.put("0",0);
        linha1.put("1",0);
        linha1.put("2",0);
        linha1.put("3",0);
        linha1.put("4",0);
        linha1.put("5",0);

        linha2.put("0",0);
        linha2.put("1",0);
        linha2.put("2",0);
        linha2.put("3",0);
        linha2.put("4",0);
        linha2.put("5",0);
        linha2.put("6",0);
        linha2.put("7",0);

        linha3.put("0",0);
        linha3.put("1",0);
        linha3.put("2",0);
        linha3.put("3",0);
        linha3.put("4",0);
        linha3.put("5",0);
        linha3.put("6",0);
        linha3.put("7",0);
        linha3.put("8",0);
        linha3.put("9",0);
        linha3.put("10",0);
    }
    
    @Override
    public synchronized boolean requestEntry(int linha, int segmento){
        switch(linha){
            case 1:
                if (segmento == (linha1.size() -1)) {
                    if (checkAvailability(linha1, segmento)) {
                        setOccupied(linha, segmento);
                        return true;
                    }
                }
                if(checkAvailability(linha1, segmento) && checkAvailability(linha1, segmento+1)){
                    setOccupied(linha, segmento);
                    return true;
                }
                break;
            case 2:
                if (segmento == (linha2.size() -1)) {
                    if (checkAvailability(linha2, segmento)) {
                        setOccupied(linha, segmento);
                        return true;
                    }
                }
                if(checkAvailability(linha2, segmento) && checkAvailability(linha2, segmento+1)){
                    setOccupied(linha, segmento);
                    return true;
                }
                break;
            case 3:
                if (segmento == (linha3.size() - 1)) {
                    if (checkAvailability(linha3, segmento)) {
                        setOccupied(linha, segmento);
                        return true;
                    }
                }
                if(checkAvailability(linha3, segmento) && checkAvailability(linha3, segmento+1)){
                    setOccupied(linha, segmento);
                    return true;
                }
                break;
        }
        return false;
    }
    
    public synchronized boolean checkAvailability(Map<String, Integer> linha, int segmento){
        if(linha.get(Integer.toString(segmento)) == 0)
            return true;
        return false;
    }
     
    @Override
    public synchronized boolean setAvailable(int linha, int segmento){
            switch(linha){
                case 1:
                    if(segmento <= linha1.size()) {
                        linha1.put(Integer.toString(segmento), 0);
                        return true;
                    }
                    break;
                case 2:
                    if(segmento <= linha2.size()){
                        linha2.put(Integer.toString(segmento), 0);
                        return true;
                    }
                    break;
                case 3:
                    if(segmento <= linha3.size()){
                        linha3.put(Integer.toString(segmento), 0);
                        return true;
                    }
                    break;
            }
            return false;
    }
     
    @Override
    public synchronized boolean setOccupied(int linha, int segmento){
        switch(linha){
            case 1:
                if(segmento <= linha1.size()){
                    linha1.put(Integer.toString(segmento), 1);
                    return true;
                }
                break;
            case 2:
                if(segmento <= linha2.size()){
                    linha2.put(Integer.toString(segmento), 1);
                    return true;
                }
                break;
            case 3:
                if(segmento <= linha3.size()){
                    linha3.put(Integer.toString(segmento), 1);
                    return true;
                }
                break;
            }
            return false;
    }
}
