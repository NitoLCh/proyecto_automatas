/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: Ene-Jun 2023    HORA: ______18:00_____ HRS
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
 *: 20/Feb/2020                     -Se implementaron los procedures del parser 
                                    recursivo predictivo del lenguaje BasicTec
 *: 16/Abr/2023                     -Se incluyó el analizador semántico
 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;
    private static final String VACIO = "VACIO";
    private static final String ERROR_TIPO = "error_tipo";
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }
    
    public String getDominio (String tipo) {
        String [] partes = tipo.split("->");
        return partes[0];
    }
    
    public String getRango (String tipo) {
        String [] partes = tipo.split("->");
        return partes[1];
    }
    
    public String getArgumentos(String tipo){
        String [] argumentos = tipo.split("x");
        return argumentos[0];
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
        Atributo programa = new Atributo();
        programa(programa);
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
    
    private void programa(Atributo programa) {
        //programa → declaraciones
    //    declaraciones_subprogramas
    //    proposiciones_optativas
    //    end {1} 
        Atributo declaraciones = new Atributo();
        Atributo declaraciones_subprogramas = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
    
        String terminales[] = {"dim", "function", "sub", "id", "call", "if", "do", "end"};
        if(estaEn(terminales)) {
            declaraciones(declaraciones);
            declaraciones_subprogramas(declaraciones_subprogramas);
            proposiciones_optativas(proposiciones_optativas);
            emparejar("end");
            
            //Accion Semántica 1
            if(analizarSemantica) {
                if(declaraciones.tipo.equals(VACIO) && 
                    declaraciones_subprogramas.tipo.equals(VACIO) && 
                    proposiciones_optativas.tipo.equals(VACIO)){
                    programa.tipo = VACIO;
                }
                else{
                    programa.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{1} : ERROR EN EL PROGRAMA" );
                }
            }   
        } else {
            error("Syntax error: El archivo no tiene end");
        }
    }

    private void declaraciones(Atributo declaraciones) {
        //declaraciones → dim lista_declaraciones declaraciones2{2} 
        Atributo lista_declaraciones = new Atributo();
        Atributo declaraciones2 = new Atributo();
        
        if(preAnalisis.equals("dim")) {
            emparejar("dim");
            lista_declaraciones(lista_declaraciones);
            declaraciones(declaraciones2);
            //Acción Semántica 2
            if(analizarSemantica) {
                if(!lista_declaraciones.tipo.equals(ERROR_TIPO) && !declaraciones2.tipo.equals(ERROR_TIPO))
                    declaraciones.tipo = VACIO;
                else{
                    declaraciones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{2} : lista_declaraciones = ERROR_TIPO o "
                                + "declaraciones2.tipo = ERROR_TIPO" );
                }
            }
        }
        else if(analizarSemantica) {
            //declaraciones → ϵ{3}
            //Acción Semántica 3
            declaraciones.tipo = VACIO;
        }
    }

    private void lista_declaraciones(Atributo lista_declaraciones) {
        //lista_declaraciones → id  as  tipo   lista_declaraciones’ {4}
        Linea_BE id = new Linea_BE();
        Atributo lista_declaraciones_prima = new Atributo();
        Atributo tipo = new Atributo();
        
        if(preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("as");
            tipo(tipo);
            lista_declaraciones_prima(lista_declaraciones_prima);
            //Acción Semántica 4
            if(analizarSemantica){
                if(cmp.ts.buscaTipo(id.entrada).equals("")){
                    cmp.ts.anadeTipo(id.entrada, tipo.tipo);
                    if(!lista_declaraciones_prima.tipo.equals(ERROR_TIPO)){
                        lista_declaraciones.tipo = tipo.tipo;
                    } 
                    else if(!lista_declaraciones.tipo.equals(ERROR_TIPO)){
                        lista_declaraciones.tipo = tipo.tipo + "x" + lista_declaraciones_prima.tipo;
                    }
                    else{
                        lista_declaraciones.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, "{4} : lista_declaraciones = ERROR_TIPO");
                    }
                } 
                else {
                    lista_declaraciones.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "{4} : Identificador ya declarado");
                }
            }
        } 
        else{
            error(String.format("syntax error in line %s: Mala declaracion de variables",
                                cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void lista_declaraciones_prima(Atributo lista_declaraciones_prima){
        //lista_declaraciones’ → , lista_declaraciones{5} 
        Atributo lista_declaraciones = new Atributo();
        if(preAnalisis.equals(",")){
            emparejar(",");
            lista_declaraciones(lista_declaraciones);
            //Acción Semántica 5
            if(analizarSemantica){
                lista_declaraciones_prima.tipo = lista_declaraciones.tipo;
            }
        }else{
            if(analizarSemantica){
                //lista_declaraciones’ → ϵ {6}
                //Acción Semánitca 6
                lista_declaraciones_prima.tipo = VACIO;
            }
        }
    }

    private void proposicion_prima(Atributo proposicion_prima) {
        //proposicion’ → ( lista_expresiones ){7}
        Atributo lista_expresiones = new Atributo();
        if(preAnalisis.equals('(')) {
            emparejar("(");
            lista_expresiones(lista_expresiones);
            emparejar(")");
            //Acción Semántica 7
            if(analizarSemantica) {
                proposicion_prima.tipo = lista_expresiones.tipo;
            }
        }else if(analizarSemantica) {
            // proposicion_prima -> empty{8}
            //Acción Semántica 8
            proposicion_prima.tipo = "VOID";
        }
    }

    private void lista_expresiones(Atributo lista_expresiones) {
        //lista_expresiones → expresion lista_expresiones’{9}
        Atributo expresion = new Atributo();
        Atributo lista_expresiones_prima = new Atributo();
        
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        if(estaEn(terminales)) {
            expresion(expresion);
            lista_expresiones_prima(lista_expresiones_prima);     
            //Acción Semántica 9
            if(analizarSemantica){
                if(!expresion.tipo.equals(ERROR_TIPO) && !lista_expresiones_prima.tipo.equals(ERROR_TIPO))
                    if(lista_expresiones_prima.tipo.equals(VACIO))
                        lista_expresiones.tipo = expresion.tipo;
                    else
                        lista_expresiones.tipo = expresion.tipo + "x" + lista_expresiones_prima.tipo;
                else {
                    lista_expresiones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{9} : ERROR en la Expresión" );
                }
            }
        }
        else {
            if (analizarSemantica){
                //lista_expresiones → ϵ
                //Acción Semántica 10
                lista_expresiones.tipo = VACIO;
            }
        }
    }

    private void lista_expresiones_prima(Atributo lista_expresiones_prima) {
        //lista_expresiones’  →  ,  expresion lista_expresiones’{11}
        Atributo expresion = new Atributo();
        Atributo lista_expresiones_prima2 = new Atributo();
        
        if(preAnalisis.equals(",")) {
            emparejar(",");
            expresion(expresion);
            lista_expresiones_prima(lista_expresiones_prima2);
            //Acción Semántica 11    
            if(analizarSemantica){
                if(!expresion.tipo.equals(ERROR_TIPO) && !lista_expresiones_prima2.tipo.equals(ERROR_TIPO))
                    if(lista_expresiones_prima2.tipo.equals(VACIO)) 
                        lista_expresiones_prima.tipo = expresion.tipo;
                    else
                        lista_expresiones_prima.tipo = expresion.tipo + "x" + lista_expresiones_prima2.tipo;
                else{
                    lista_expresiones_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{11} : ERROR en la Expresión" );
                }
            }
        }
        else{ 
            if(analizarSemantica){
                //lista_expresiones’ → ϵ {12}
                //Acción Semántica 12
                lista_expresiones_prima.tipo = VACIO;
            }
        }
    }

    private void condicion(Atributo condicion) {
        //condicion → expresion  oprel   expresion {13}
        Atributo expresion2 = new Atributo();
        Atributo expresion3 = new Atributo();
        
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        if(estaEn(terminales)){
            expresion(expresion2);
            emparejar("oprel");
            expresion(expresion3);
            //Acción Semántica 13
            if(analizarSemantica) {
                if(expresion2.tipo.equals(expresion3.tipo)){
                    condicion.tipo = "BOOLEAN";
                }
                else if(expresion2.tipo.equals("INTEGER") && expresion3.tipo.equals("SINGLE") ||
                        expresion2.tipo.equals("SINGLE") && expresion3.tipo.equals("INTEGER")){
                    condicion.tipo = "BOOLEAN";
                }else{
                    condicion.tipo = "ERROR_TIPO";
                    cmp.me.error( cmp.ERR_SEMANTICO, "{13} : Las expresiones no concuerdan" );
                }
            }
        }else{
            error(String.format("syntax error in line %s: Condición no válida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void expresion(Atributo expresion){
        //expresion → termino{14}  expresion’{15} |  
        Atributo termino = new Atributo();
        Atributo expresion_prima = new Atributo();
        String terminales[] = {"id", "num", "num.num", "("};
        if(estaEn(terminales)){
            termino(termino);
            //Acción Semántica 14
            if(analizarSemantica){
                expresion_prima.h = termino.tipo;
            }
            expresion_prima(expresion_prima);
            //Acción Semántica 15
            if(analizarSemantica){
                if(!expresion_prima.h.equals(ERROR_TIPO) && 
                     !expresion_prima.tipo.equals(ERROR_TIPO))
                    expresion.tipo = expresion_prima.tipo;
                else {
                    expresion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{15} : ERROR de Tipos" );
                }
            }
        }else if(preAnalisis.equals("literal")){
            emparejar("literal");
            //expresion → literal{16}
            //Acción Semántica 16
            if(analizarSemantica)
                expresion.tipo = "STRING";
        }else{
            error(String.format("syntax error in line %s: Expersión no válida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void expresion_prima(Atributo expresion_prima) {
        //expresion’ → opsuma termino{17} expresion’{18}
        Atributo termino = new Atributo();
        Atributo expresion_prima2  = new Atributo();
        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            termino(termino);
            //Acción Semántica 17
            if(analizarSemantica){
                if(expresion_prima.h.equals(termino.tipo))
                    expresion_prima2.h = termino.tipo;
                else if (expresion_prima.h.equals("SINGLE") && termino.tipo.equals("INTEGER") ||
                          expresion_prima.h.equals("INTEGER") && termino.tipo.equals("SINGLE")) 
                    expresion_prima2.h = "SINGLE";
                else{
                    expresion_prima2.h = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{17} : ERROR de Expresion" );
                }
            }
            expresion_prima(expresion_prima2);
           //Acción Semántica 18
           if(analizarSemantica){
                if(!expresion_prima2.h.equals(ERROR_TIPO) && !expresion_prima2.tipo.equals(ERROR_TIPO))
                    expresion_prima.tipo = expresion_prima2.tipo;
                else {
                    expresion_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{18} : ERROR de Expresion_prima" );
                    }
            }
        }else{
            if(analizarSemantica) {
                //expresion’ → ϵ{19}
                expresion_prima.tipo = expresion_prima.h;
            }
        }
    }

    private void termino(Atributo termino) {
        //termino → factor{20} termino’ {21}
        Atributo factor = new Atributo();
        Atributo termino_prima = new Atributo();
        
        String terminales[] = {"id", "num", "num.num", "("};
        if(estaEn(terminales)){
            factor(factor);
            //Acción Semántica 20
            if(analizarSemantica) {
                termino_prima.h = factor.tipo;
            }
            termino_prima(termino_prima);
            //Acción Semántica 21
            if(analizarSemantica) {
                if(!termino_prima.h.equals(ERROR_TIPO) &&
                   !termino_prima.tipo.equals(ERROR_TIPO)) 
                    termino.tipo = termino_prima.tipo;
                else {
                    termino.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{21} : ERROR de Tipos" );
                }
            }
        }else{
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void termino_prima(Atributo termino_prima) {
        //termino’ → opmult  factor{22}  termino’{23}
        Atributo factor = new Atributo();
        Atributo termino_prima2 = new Atributo();
        
        if(preAnalisis.equals("opmult")){
            emparejar("opmult");
            factor(factor);
            //Acción Semántica 22
            if(analizarSemantica){
                if(termino_prima.h.equals(factor.tipo)){
                    termino_prima2.h = factor.tipo;
                }
                else if(termino_prima.h.equals("SINGLE") && factor.tipo.equals("INTEGER") ||
                          termino_prima.h.equals("INTEGER") && factor.tipo.equals("SINGLE")){
                    termino_prima2.h = "SINGLE";
                }
                else if(termino_prima.h.equals(getRango(factor.tipo))) {
                    termino_prima2.h = getRango(factor.tipo);
                }
                else {
                    termino_prima2.h = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{22} : ERROR de Tipos" + "En linea: " + cmp.be.preAnalisis.numLinea);
                }
            }
            termino_prima(termino_prima2);
            //Acción Semántica 23
            if(analizarSemantica) {
                if(!termino_prima2.h.equals(ERROR_TIPO) && !termino_prima2.tipo.equals(ERROR_TIPO) )
                    termino_prima.tipo = termino_prima2.tipo;
                else {
                    termino_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{23} : ERROR de Tipos" );
                }
            }
        } else {
            if(analizarSemantica){
                //termino’ →  ϵ {24}
                //Acción Semántica 24
                termino_prima.tipo = termino_prima.h;
            }
        }
    }

    private void factor(Atributo factor) {
        //factor → id  factor’{25} | num{26} | num.num{27}  |  ( expresion ){28}
        Linea_BE id = new Linea_BE();
        Atributo factor_prima = new Atributo();
        Atributo expresion = new Atributo();
        
        if(preAnalisis.equals("id")){
            id = cmp.be.preAnalisis;
            emparejar("id");
            factor_prima(factor_prima);
            //Acción Semántica 25
            if(analizarSemantica){
                if(factor_prima.tipo.equals(VACIO)){
                    factor.tipo = cmp.ts.buscaTipo(id.entrada);
                }else if(getDominio(cmp.ts.buscaTipo(id.entrada))
                        .equals(getArgumentos(factor_prima.tipo)))
                    factor.tipo = getRango(cmp.ts.buscaTipo(id.entrada));
                else {
                    factor.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{25} : ERROR de Tipos. \n"+ "Comparación " +
                            getDominio(cmp.ts.buscaTipo(id.entrada)) + " con " + factor_prima.tipo);
                }
            }
        } else if (preAnalisis.equals("num")) {
            emparejar("num");
            //Acción Semántica 26
            if(analizarSemantica){
                factor.tipo = "INTEGER";
            }
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
            //Acción Semántica 27
            if ( analizarSemantica )
                factor.tipo = "SINGLE";
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            expresion(expresion);
            emparejar(")");
            //Acción Semántica 28
            if(analizarSemantica)
                factor.tipo = expresion.tipo;
        } else {
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void factor_prima(Atributo factor_prima) {
        //factor’ → ( lista_expresiones ){29}
        Atributo lista_expresiones = new Atributo();
        if(preAnalisis.equals("(")){
            emparejar("(");
            lista_expresiones(lista_expresiones);
            emparejar(")");
            //Acción Semántica 29
            if(analizarSemantica){
                factor_prima.tipo = lista_expresiones.tipo;
            }
        }else{ 
            if(analizarSemantica)
                //factor’ → ϵ{30}
                //Acción Semántica 30
                factor_prima.tipo = VACIO;
        }
    }

    private void tipo(Atributo tipo) {
        //tipo → integer{31}  | single{32}  | string{33}
        if(preAnalisis.equals("integer")) {
            emparejar("integer");
            //Acción Semántica 31
            if(analizarSemantica)
                tipo.tipo = "INTEGER";
        }else if(preAnalisis.equals("single")) {
            emparejar("single");
            //Acción Semántica 32
            if(analizarSemantica)
                tipo.tipo = "SINGLE";
        }else if (preAnalisis.equals("string")) {
            emparejar("string");
            //Acción Semántica 33
            if(analizarSemantica)
                tipo.tipo = "STRING";
        }else {
            error(String.format("syntax error in line %s: Tipo de dato inválido",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void declaraciones_subprogramas(Atributo declaraciones_subprogramas) {
        //declaraciones_subprogramas → declaracion_subprograma  declaraciones_subprogramas{34}  
        Atributo declaracion_subprograma = new Atributo();
        Atributo declaraciones_subprogramas2 = new Atributo();
        
        String terminales[] = {"function", "sub"};
        if(estaEn(terminales)){
            declaracion_subprograma(declaracion_subprograma);
            declaraciones_subprogramas(declaraciones_subprogramas2);
            //Acción Semántica 34
            if(analizarSemantica){
                if(declaracion_subprograma.tipo.equals(VACIO) &&
                    declaraciones_subprogramas2.tipo.equals(VACIO)){
                    declaraciones_subprogramas.tipo = VACIO;
                } else {
                    declaraciones_subprogramas.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{34} : declaracion_subprogrma = ERROR_TIPO o"
                                                                  + "declaraciones_subprogramas1 = ERROR_TIPO" );
                }
            }
        }else if(analizarSemantica)
            //declaraciones_subprogramas →  ϵ{35}
            //Acción Semántica 35
            declaraciones_subprogramas.tipo = VACIO;
    }
    
    private void declaracion_subprograma(Atributo declaracion_subprograma){
        //declaracion_subprograma → declaracion_funcion{36}  |  declaracion_subrutina{37}
        Atributo declaracion_funcion = new Atributo();
        Atributo declaracion_subrutina = new Atributo();
        if(preAnalisis.equals("function")){
            declaracion_funcion(declaracion_funcion);
            //Acción Semántica 36
            if(analizarSemantica)
                declaracion_subprograma.tipo = declaracion_funcion.tipo;
        }else if(preAnalisis.equals("sub")){
            declaracion_subrutina(declaracion_subrutina);
            //Acción Semántica 37
            if(analizarSemantica)
                declaracion_subprograma.tipo = declaracion_subrutina.tipo;
        }else{
            error(String.format("syntax error in line %s: Mala declaración de subrprograma",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void declaracion_funcion(Atributo declaracion_funcion) {
        //declaracion_funcion → function id  argumentos  as tipo  proposiciones_optativas{38} end function
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo();
        Atributo tipo = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if(preAnalisis.equals("function")) {
            emparejar("function");
            id = cmp.be.preAnalisis;
            emparejar("id");
            argumentos(argumentos);
            emparejar("as");
            tipo(tipo);
            proposiciones_optativas(proposiciones_optativas);
            //Acción Semántica 38
            if(analizarSemantica){
                if(cmp.ts.buscaTipo(id.entrada).equals("")){
                    if(!argumentos.tipo.equals(ERROR_TIPO)){
                        cmp.ts.anadeTipo(id.entrada , argumentos.tipo + "->" + tipo.tipo);
                        if(proposiciones_optativas.tipo.equals(VACIO)) 
                            declaracion_funcion.tipo = VACIO;
                        else{
                            declaracion_funcion.tipo = ERROR_TIPO;
                            cmp.me.error(cmp.ERR_SEMANTICO, "{38} : ERROR en proposiciones_optativas");
                        }
                    }else{
                        declaracion_funcion.tipo = ERROR_TIPO;
                        cmp.me.error(cmp.ERR_SEMANTICO, "{38} : ERROR en argumentos");
                    }
                }else{
                    declaracion_funcion.tipo = ERROR_TIPO;
                    cmp.me.error(cmp.ERR_SEMANTICO, "{38} : Función previamente declarada");
                }
            }
            emparejar("end");
            emparejar("function");
        }else{
            error(String.format("syntax error in line %s: Mala declaración de función",
                    cmp.be.preAnalisis.numLinea));
        }

    }

    private void declaracion_subrutina(Atributo declaracion_subrutina) {
        //declaracion_subrutina → sub id argumentos  proposiciones_optativas {39} end sub
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if(preAnalisis.equals("sub")) {
            emparejar("sub");
            id = cmp.be.preAnalisis;
            emparejar("id");
            argumentos(argumentos);
            proposiciones_optativas(proposiciones_optativas);
            //Acción Semántica 39
            if(analizarSemantica){
                if(cmp.ts.buscaTipo(id.entrada).equals("")){
                    if (!argumentos.tipo.equals(ERROR_TIPO)){
                        cmp.ts.anadeTipo(id.entrada , argumentos.tipo + "->" + "VOID");
                        if(proposiciones_optativas.tipo.equals(VACIO)) 
                            declaracion_subrutina.tipo = VACIO;
                        else{
                            declaracion_subrutina.tipo = ERROR_TIPO;
                            cmp.me.error(cmp.ERR_SEMANTICO, "{39} : ERROR en proposiciones_optativas");
                        }
                    }else{
                        declaracion_subrutina.tipo = ERROR_TIPO;
                        cmp.me.error(cmp.ERR_SEMANTICO, "{39} : ERROR en argumentos");
                    }
                }else{
                    declaracion_subrutina.tipo = ERROR_TIPO;
                    cmp.me.error(cmp.ERR_SEMANTICO, "{39} : Función previamente declarada");
                }
            }
            emparejar("end");
            emparejar("sub");
        }else{
            error(String.format("syntax error in line %s: Mala declaración de subrutina.",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void argumentos(Atributo argumentos) {
        //argumentos → ( lista_declaraciones ) {40}
        Atributo lista_declaraciones = new Atributo();
        if(preAnalisis.equals("(")) {
            emparejar("(");
            lista_declaraciones(lista_declaraciones);
            emparejar(")");
            //Acción Semántica 40
            if(analizarSemantica)
                argumentos.tipo = lista_declaraciones.tipo;
        }else{ 
            if(analizarSemantica)
                //argumentos →  ϵ {41}
                //Acción Semántica 41
                argumentos.tipo = "VOID";
        }
    }

    private void proposiciones_optativas(Atributo proposiciones_optativas) {
        //proposiciones_optativas → proposicion  proposiciones_optativas {42}  
        Atributo proposicion = new Atributo();
        Atributo proposiciones_optativas2 = new Atributo();
        
        String terminales[] = {"id", "call", "if", "do"};
        if (estaEn(terminales)) {
            proposicion(proposicion);
            proposiciones_optativas(proposiciones_optativas2);
            //Acción Semántica 42
            if(analizarSemantica){
                if(proposicion.tipo.equals(VACIO) && 
                     proposiciones_optativas2.tipo.equals(VACIO)){
                    proposiciones_optativas.tipo = VACIO;
                }else{
                    proposiciones_optativas.tipo = ERROR_TIPO;
                    cmp.me.error(cmp.ERR_SEMANTICO, "{42} : Error en las proposiciones");
                }
            }
        }else{ 
            if(analizarSemantica)
            //proposiciones_optativas → ϵ{43}
            //Acción Semántica 43
            proposiciones_optativas.tipo = VACIO;
        }
    }

    private void proposicion(Atributo proposicion) {
          //proposicion → id  opasig expresion {44} |  call  id  proposicion’{45}  |
  //        if condicion then proposiciones_optativas else proposiciones_optativas {46} end if | 
  //        do while condicion  proposiciones_optativas {47} loop 
        
        Linea_BE id = new Linea_BE();
        Atributo expresion = new Atributo();
        Atributo proposicion_prima = new Atributo();
        Atributo condicion = new Atributo();
        Atributo proposiciones_optativas2 = new Atributo();
        Atributo proposiciones_optativas3 = new Atributo();
        Atributo condicion2 = new Atributo();
        Atributo proposiciones_optativas4 = new Atributo();
        
        if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("opasig");
            expresion(expresion);
            //Acción Semántica 44
            if(analizarSemantica){
                if(cmp.ts.buscaTipo(id.entrada).equals(expresion.tipo)){
                    proposicion.tipo = VACIO;
                }
                else if(cmp.ts.buscaTipo(id.entrada).equals("SINGLE") && expresion.tipo.equals("INTEGER")){
                    proposicion.tipo = VACIO;
                }
                else{
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{44} : Tipo y expresion no concuerdan En Linea\n" + cmp.be.preAnalisis.numLinea  );
                }
            }
        }else if (preAnalisis.equals("call")) {
            emparejar("call");
            id = cmp.be.preAnalisis;
            emparejar("id");
            proposicion_prima(proposicion_prima);
            //Acción Semántica 45
            if(analizarSemantica){
                if(proposicion_prima.tipo.equals(getDominio(cmp.ts.buscaTipo(id.entrada)))) 
                    proposicion.tipo = VACIO;
                else{
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error(cmp.ERR_SEMANTICO, "{45} : Argumentos no concuerdan con la tabla de simbolos");
                }
                    
            }
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            condicion(condicion);
            emparejar("then");
            proposiciones_optativas(proposiciones_optativas2);
            emparejar("else");
            proposiciones_optativas(proposiciones_optativas3);
            //Acción Semántica 46
            if(analizarSemantica){
                if(condicion.tipo.equals("BOOLEAN")){
                    if(proposiciones_optativas2.tipo.equals(VACIO) &&
                         proposiciones_optativas3.tipo.equals(VACIO)) 
                        proposicion.tipo = VACIO;
                    else{
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error(cmp.ERR_SEMANTICO, "{46} : Error en las proposiciones");
                    }
                        
                } else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error(cmp.ERR_SEMANTICO, "{46} : Expresion no es un BOOLEAN");
                }
            }
            emparejar("end");
            emparejar("if");
        }
        else if (preAnalisis.equals("do")) {
            emparejar("do");
            emparejar("while");
            condicion(condicion2);
            proposiciones_optativas(proposiciones_optativas4);
            //Acción Semántica 47
            if(analizarSemantica){
                if(condicion2.tipo.equals("BOOLEAN")){
                    if(proposiciones_optativas4.tipo.equals(VACIO))
                        proposicion.tipo = VACIO;
                    else{
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "{47} : Error en la proposición" );
                    }
                }else{
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "{47} : Expresion no es un BOOLEAN" );
                }
            }
            emparejar("loop");
        } else{
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }

    private void propsicion_prima(Atributo proposicion_prima) {
        //proposicion’ → ( lista_expresiones ) {48} 
        Atributo lista_expresiones = new Atributo();
        if(preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones(lista_expresiones);
            emparejar(")");
            //Acción Semántica 48
            if(analizarSemantica)
                proposicion_prima.tipo = lista_expresiones.tipo;
        }else if(analizarSemantica)
            //proposicion’ →  ϵ {49}
            //Acción Semántica 49
            proposicion_prima.tipo = "VOID";
        //empty
    }
}
//------------------------------------------------------------------------------
//::
