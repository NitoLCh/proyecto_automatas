/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    private void programa(){
    //
    String terminales[] = {"dim", "function", "sub", "id", "call","if","do"};
    ArrayList<String> primerosPrograma = new ArrayList<String>(Arrays.asList(terminales));
        
    if (primerosPrograma.contains(preAnalis))
        {
            declaraciones();
           declaraciones_subprogramas();
           emparejar("end");
        }
    } else
        error();

    private void declaraciones(){
    //
    if preAnalisis.equals("dim")
      {
       emparejar("dim");
       lista_declaraciones();
       declaraciones();
      }
    else
     //empty
     }

    private void lista_declaraciones();
    {
    if(preanalisis.equals("id"))
      {
       emparejar("id");
       emparejar ("as");
       tipo();
       lista_declaraciones_prima();
      }
      else
           error();
    }
       
    private void proposicion_prima(){
        //proposicion’ → ( lista_expresiones ) | ϵ
        if(preAnalisis.equals('(') ){
            emparejar("(");
            lista_expresiones();
             emparejar(")");
        }
        //empty
    }
    
    private void lista_expresiones(){
        //lista_expresiones → expresion lista_expresiones’ | ϵ
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        ArrayList<String> primerosListaExpresiones = new ArrayList<String>(Arrays.asList(terminales));
        if(primerosListaExpresiones.contains(preAnalisis)){
            expresion();
            lista_expresiones_prima();
        }
        //empty    
}
    
    private void lista_expresiones_prima(){
        if(preAnalisis.equals(",")){
            emparejar(",");
            lista_declaraciones();
        }            
    }
   
    private void tipo()
    {
     if(preAnalisis.equals("integer"))
        emparejar("integer");
     if(preAnalisis.equals("single"))
        emparejar("single");
     if(preAnalisis.equals("string"))
        emparejar("string");
     else
        error();
    }

     private void declaraciones_subprogramas()
    {
      if(preAnalisis.equals("function"))
          declaracion_funcion();
      else if(preAnalisis.equals("sub"))
          declaracion_subrutina();
      else
          error();
    } 

     private void declaracion_funcion()
     {
      if(preAnalisis.equals("function"))
      {
       emparejar("function");
       emparejar("id");
       argumentos();
       emparejar("as");
       tipo();
       proposiciones_optativas();
       emparejar("end");
       emparejar("function");
      }else
        error();

    }  

    private void declaracion_subrutina()
    {
     if(preAnalisis.equals("sub")){
        emparejar("sub");
        emparejar("id");
        argumentos();
        emparejar("as");
        tipo();
        proposiciones_optativas();
        emparejar("end");
        emparejar("sub");
     }else
         error();    
    }
   
    private void argumentos()
    {
       if(preAnalis.equals("("))
         {
           emparejar("(");
           lista_declaraciones();
           emparejar(")");
         }
    }
    
    private void proposiciones_optativas()
    {
     String terminales[] = {"id", "call", "if", "do"};
    ArrayList<String> primerosPropOpta = new ArrayList<String>(Arrays.asList(terminales));
        
    if (primerosPropOpta.contains(preAnalis))
        {
         proposicion();
         poroposiciones_optativas();
        }
    else
        error()

    } 
   
    private void proposicion()
    {   
      if(preAnalisis.equals("id"))
       {
        emparejar("id");
        emparejar("opasig");
        expresion();
       }else if(preAnalisis.equals("call"))
       {
        emparejar("call");
        emparejar("id");
        proposicion_prima();
       }else if(preAnalisis.equals("if"))
       {
        emparejar("if");
        condicion();
        emparejar("then");
        proposiciones_optativas();
        emparejar("else");
        proposiciones_optativas();
        emparejar("end");
        emparejar("if");
       } if(preAnalisis.equals("do"))
       {
        emparejar("do");
        emparejar("while");
        condicion();
        proposiciones_optativas();
        emparejar("loop");
       }else
        error();


    }  
    private void propsicion_prima()
    {
     if(preAnalisis.equals("("))
       {
         emparejar("(");
         lista_expresiones();
         emparejar(")"); 
       } 
      else
       //empty

    }


   private void lista_expresiones()
   {
    String terminales[] = {"id", "num", "num.num", "(","literal"};
    ArrayList<String> primerosListaExp = new ArrayList<String>(Arrays.asList(terminales));
        
    if (primerosListaExp.contains(preAnalis))
       {
        expresion();
        lista_expresiones_prima();
       }else
    //empty 
      
   } 
   private void  lista_expresiones_prima()
   {
      if(preAnalisis.equals(","))
       {
        emparejar(",");
        expresion();
        lista_expresiones();
       }else
       // empty 
 
   }
   

   private void condicion(){
   String terminales[] = {"id", "num", "num.num", "(","literal"};
    ArrayList<String> primerosCondicion = new ArrayList<String>(Arrays.asList(terminales));
        
    if (primerosCondicion.contains(preAnalis))
    {
      expresion();
      emparejar("oprel");
      expresion();
    }
   } 

}
//------------------------------------------------------------------------------
//::