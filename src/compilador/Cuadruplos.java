/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.ArrayList;

public class Cuadruplos {
    public ArrayList<Cuadruplo> cuadruplos;
    private Compilador cmp;
    
    public Cuadruplos(Compilador c){
        cmp = c;
        cuadruplos = new ArrayList<>();
    }
    
    public void inicializar(){
        vaciar();
    }
    
    public void agregar(Cuadruplo cuadruplo){
        cuadruplos.add(cuadruplo);
    }
    
    public void vaciar(){
        cuadruplos.clear();
    }
    
    public int getTamano(){
        return cuadruplos.size();
    }
    
    public ArrayList<Cuadruplo> getCuadruplos(){
        return cuadruplos;
    }
}

