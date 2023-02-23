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
    private boolean analizarSemantica = false;
    private String preAnalisis;

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
        programa();
    }

    //--------------------------------------------------------------------------
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
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
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

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
    private boolean estaEn(String[] terminales) {
        return Arrays.asList(terminales).contains(preAnalisis);
    }
    
    private void programa() {
        //programa → declaraciones
    //    declaraciones_subprogramas
    //    proposiciones_optativas
    //    end
        String terminales[] = {"dim", "function", "sub", "id", "call", "if", "do", "end"};
        if (estaEn(terminales)) {
            declaraciones();
            declaraciones_subprogramas();
            proposiciones_optativas();
            emparejar("end");
        } else {
            error("Syntax error: El archivo no tiene end");
        }
    }

    private void declaraciones() {
        //declaraciones → dim lista_declaraciones declaraciones | ϵ
        if (preAnalisis.equals("dim")) {
            emparejar("dim");
            lista_declaraciones();
            declaraciones();
        }
        //empty
    }

    private void lista_declaraciones() {
        //lista_declaraciones → id  as  tipo   lista_declaraciones’
        if (preAnalisis.equals("id")) {
            emparejar("id");
            emparejar("as");
            tipo();
            lista_declaraciones_prima();
        } else {
            error(String.format("syntax error in line %s: Mala declaracion de variables",
                    cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void lista_declaraciones_prima(){
        //lista_declaraciones’ → , lista_declaraciones | ϵ
        if(preAnalisis.equals(",")){
            emparejar(",");
            lista_declaraciones();
        }
        //empty
    }

    private void proposicion_prima() {
        //proposicion’ → ( lista_expresiones ) | ϵ
        if (preAnalisis.equals('(')) {
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }
        //empty
    }

    private void lista_expresiones() {
        //lista_expresiones → expresion lista_expresiones’ | ϵ
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        if (estaEn(terminales)) {
            expresion();
            lista_expresiones_prima();
        }
        //empty    
    }

    private void lista_expresiones_prima() {
        //lista_expresiones’  →  ,  expresion lista_expresiones’  | ϵ
        if (preAnalisis.equals(",")) {
            emparejar(",");
            expresion();
            lista_expresiones_prima();
        }
        //empty
    }

    private void condicion() {
        //condicion → expresion  oprel   expresion
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        if (estaEn(terminales)) {
            expresion();
            emparejar("oprel");
            expresion();
        } else {
            error(String.format("syntax error in line %s: Condición no válida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void expresion() {
        //expresion → termino  expresion’ |  literal
        String terminales[] = {"id", "num", "num.num", "("};
        if (estaEn(terminales)) {
            termino();
            expresion_prima();
        } else if (preAnalisis.equals("literal")) {
            emparejar("literal");
        } else {
            error(String.format("syntax error in line %s: Expersión no válida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void expresion_prima() {
        //expresion’ → opsuma termino expresion’  | ϵ
        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            termino();
            expresion_prima();
        }
        //empty
    }

    private void termino() {
        //termino → factor   termino’
        String terminales[] = {"id", "num", "num.num", "("};
        if (estaEn(terminales)) {
            factor();
            termino_prima();
        } else {
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void termino_prima() {
        //termino’ → opmult  factor  termino’ | ϵ
        if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
            factor();
            termino_prima();
        }
        //empty
    }

    private void factor() {
        //factor → id  factor’ | num | num.num  |  ( expresion )
        if (preAnalisis.equals("id")) {
            emparejar("id");
            factor_prima();
        } else if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            expresion();
            emparejar(")");
        } else {
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void factor_prima() {
        //factor’ → ( lista_expresiones ) | ϵ
        if (preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }
        //empty
    }

    private void tipo() {
        //tipo → integer  | single  | string
        if (preAnalisis.equals("integer")) {
            emparejar("integer");
        } else if (preAnalisis.equals("single")) {
            emparejar("single");
        }
        else if (preAnalisis.equals("string")) {
            emparejar("string");
        } else {
            error(String.format("syntax error in line %s: Tipo de dato inválido",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void declaraciones_subprogramas() {
        //declaraciones_subprogramas → declaracion_subprograma  declaraciones_subprogramas  | ϵ
        String terminales[] = {"function", "sub"};
        
        if(estaEn(terminales)){
            declaracion_subprograma();
            declaraciones_subprogramas();
        }
        //empty
    }
    
    private void declaracion_subprograma(){
        //declaracion_subprograma → declaracion_funcion  |  declaracion_subrutina
        if(preAnalisis.equals("function")){
            declaracion_funcion();
        }
        else if(preAnalisis.equals("sub")){
            declaracion_subrutina();
        }
        else{
            error(String.format("syntax error in line %s: Mala declaración de subrprograma",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void declaracion_funcion() {
        //declaracion_funcion → function id  argumentos  as tipo  proposiciones_optativas  end function
        if (preAnalisis.equals("function")) {
            emparejar("function");
            emparejar("id");
            argumentos();
            emparejar("as");
            tipo();
            proposiciones_optativas();
            emparejar("end");
            emparejar("function");
        } else {
            error(String.format("syntax error in line %s: Mala declaración de función",
                    cmp.be.preAnalisis.numLinea));
        }

    }

    private void declaracion_subrutina() {
        //declaracion_subrutina → sub id argumentos  proposiciones_optativas  end sub
        if (preAnalisis.equals("sub")) {
            emparejar("sub");
            emparejar("id");
            argumentos();
            proposiciones_optativas();
            emparejar("end");
            emparejar("sub");
        } else {
            error(String.format("syntax error in line %s: Mala declaración de subrutina.",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void argumentos() {
        //argumentos 	→ ( lista_declaraciones ) | ϵ
        if (preAnalisis.equals("(")) {
            emparejar("(");
            lista_declaraciones();
            emparejar(")");
        }
        //empty
    }

    private void proposiciones_optativas() {
        //proposiciones_optativas → proposicion  proposiciones_optativas | ϵ
        String terminales[] = {"id", "call", "if", "do"};
        if (estaEn(terminales)) {
            proposicion();
            proposiciones_optativas();
            } else {
            //empty
        }
    }

    private void proposicion() {
          //proposicion → id  opasig expresion |  call  id  proposicion’  |
  //        if condicion then proposiciones_optativas else proposiciones_optativas end if | 
  //        do while condicion  proposiciones_optativas  loop 
        if (preAnalisis.equals("id")) {
            emparejar("id");
            emparejar("opasig");
            expresion();
        } else if (preAnalisis.equals("call")) {
            emparejar("call");
            emparejar("id");
            proposicion_prima();
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            condicion();
            emparejar("then");
            proposiciones_optativas();
            emparejar("else");
            proposiciones_optativas();
            emparejar("end");
            emparejar("if");
        }
        else if (preAnalisis.equals("do")) {
            emparejar("do");
            emparejar("while");
            condicion();
            proposiciones_optativas();
            emparejar("loop");
        } else {
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void propsicion_prima() {
        //proposicion’ → ( lista_expresiones ) | ϵ
        if (preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }
        //empty
    }

}
//------------------------------------------------------------------------------
//::
