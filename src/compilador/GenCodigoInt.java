/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_BE;
import java.util.Arrays;
import java.util.Stack;


public class GenCodigoInt {
  public static final int NIL = 0;
    private Compilador cmp;
    private int consecutivoTemp;
    private boolean analizarSemantica = false;
    private String preAnalisis;
    private static final String VACIO = "VACIO";
    private static final String ERROR_TIPO = "error_tipo";
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
     
    //--------------------------------------------------------------------------
    public int getTemp(){
        return consecutivoTemp;
    }
            
            
    
    public void generar () {
        consecutivoTemp = 1;
         InfijoC3D("( ( xxxx / ( 7 - ( yyy + 1 ) ) ) * 3 ) - ( zz + ( 1 + x ) ) ");
        // InfijoC3D("((xx/(7-(yy+1)))*3)-(zz+(1+x))");
        
       
    }    

    //--------------------------------------------------------------------------

    private void emite ( String c3d ) {
    }
    
    //--------------------------------------------------------------------------
    //************EMPAREJAR**************//
    private void emparejar ( String t ) {
	if (cmp.be.preAnalisis.complex.equals ( t ) )
		cmp.be.siguiente ();
	else
		errorEmparejar ( "Se esperaba " + t + " se encontró " +
                                 cmp.be.preAnalisis.lexema );
    }	
	
    //--------------------------------------------------------------------------
    private String tempnuevo() {
        return "t" + consecutivoTemp++;
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------

    private void errorEmparejar ( String _token ) {
        String msjError = "ERROR SINTACTICO: ";
              
        if ( _token.equals ( "id" ) )
            msjError += "Se esperaba un identificador" ;
        else if ( _token.equals ( "num" ) )
            msjError += "Se esperaba una constante entera" ;
        else if ( _token.equals ( "num.num" ) )
            msjError += "Se esperaba una constante real";
        else if ( _token.equals ( "literal" ) )
            msjError += "Se esperaba una literal";
        else if ( _token.equals ( "oparit" ) )
            msjError += "Se esperaba un Operador Aritmetico";
        else if ( _token.equals ( "oprel" ) )
            msjError += "Se esperaba un Operador Relacional";
        else 
            msjError += "Se esperaba " + _token;
                
        cmp.me.error ( Compilador.ERR_SINTACTICO, msjError );    
    }            

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
	
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
 
    private void error ( String _token ) {
        cmp.me.error ( cmp.ERR_SINTACTICO,
         "ERROR SINTACTICO: en la produccion del simbolo  " + _token );
    }
 
    private String c3dGen (String posfija)
    {
        
        String c3d="";
             return c3d;
    }
    // Fin de error
    //--------------------------------------------------------------------------
     private boolean estaEn(String[] terminales) {
        return Arrays.asList(terminales).contains(preAnalisis);
    }
     
     public String getDominio (String tipo) {
        String [] partes = tipo.split("->");
        return partes[0];
    }
    
    public String getRango (String tipo) {
        String [] partes = tipo.split("->");
        if(partes.length > 1)
            return partes[1];
        return partes[0];
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
                    if(lista_declaraciones_prima.tipo.equals(VACIO)){
                        lista_declaraciones.tipo = tipo.tipo;
                    } 
                    else if(!lista_declaraciones.tipo.equals(ERROR_TIPO)){
                        lista_declaraciones.tipo = tipo.tipo + "x" + lista_declaraciones_prima.tipo;
                    }else{
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
           
            expresion_prima(expresion_prima);
           
            
        }else if(preAnalisis.equals("literal")){
            emparejar("literal");
            //expresion → literal{16}
            
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
           
            expresion_prima(expresion_prima2);
           
        }else{
            if(analizarSemantica) {
                //expresion’ → ϵ{19}
                
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
                System.out.println("Factor.tipo = " + factor.tipo);
                //System.out.println("getRango.tipo = " + getRango(factor.tipo));
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
                System.out.println("factor prima: " + factor_prima.tipo);
                if(factor_prima.tipo.equals(VACIO)){
                    factor.tipo = cmp.ts.buscaTipo(id.entrada);
                    System.out.println("tipo: " + cmp.ts.buscaTipo(id.entrada));
                }else if(getDominio(cmp.ts.buscaTipo(id.entrada))
                        .equals(factor_prima.tipo))
                    factor.tipo = getRango(cmp.ts.buscaTipo(id.entrada));
                else {
                    factor.tipo = ERROR_TIPO;
                    if(factor_prima.tipo.equals(""))
                        cmp.me.error(cmp.ERR_SEMANTICO, "{25}: No se ha declarado variable en linea " + (cmp.be.preAnalisis.numLinea-1));
                    else
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
                    if(proposicion.tipo.equals("") || proposiciones_optativas2.tipo.equals(""))
                        cmp.me.error(cmp.ERR_SEMANTICO, "{42} : Error en las proposiciones: No se ha declarado variable en linea " + (cmp.be.preAnalisis.numLinea-1));
                    else
                        cmp.me.error(cmp.ERR_SEMANTICO, "{42} : Error en las proposiciones: \n Comparando"
                                    + proposicion.tipo + " con " + proposiciones_optativas2.tipo);
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
            // ----------------------Accion Semantica 1----------------
            emite(id + ":=" + expresion.Lugar );
            //---------------------------Fin---------------------------
        }else if (preAnalisis.equals("call")) {
            emparejar("call");
            id = cmp.be.preAnalisis;
            emparejar("id");
            proposicion_prima(proposicion_prima);
            //Acción Semántica 45
           
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            // ----------------------Accion Semantica 2----------------
            proposicion.siguiente = tempnuevo();
            condicion.verdadera = tempnuevo();
            condicion.falsa = tempnuevo();
            proposiciones_optativas2.siguiente = proposicion.siguiente;
            // ----------------------Fin----------------
            //
            condicion(condicion);
            emparejar("then");
            proposiciones_optativas(proposiciones_optativas2);
            
             // ----------------------Accion Semantica 3----------------
             emite ( proposicion.falsa + ":" );
             // ---------------------Fin----------------
            emparejar("else");
            proposiciones_optativas(proposiciones_optativas3);
            // ----------------------Accion Semantica 4----------------
            emite ( proposicion.siguiente + ":" );
            // ----------------------fin----------------
            //Acción Semántica 46
            
            emparejar("end");
            emparejar("if");
        }
        else if (preAnalisis.equals("do")) {
            emparejar("do");
            emparejar("while");
              // ----------------------Accion Semantica 5----------------
                proposicion.comienzo = tempnuevo();
                proposicion.siguiente = tempnuevo(); 
                condicion.verdadera = tempnuevo();
                condicion.falsa = proposicion.siguiente;
                // *NO SUPE QUE ONDA CON ESTO * proposiciones_optativas3.siguiente = proposicion.comienzo  emite(proposicion.comienzo + ":" );
               // ----------------------Fin----------------
            condicion(condicion2);
            proposiciones_optativas(proposiciones_optativas4);
            //----------------------Accion Semantica 6----------------
              emite ( "goto" + proposicion.comienzo );
              emite ( condicion.falsa + ":" );

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

//------------------------------------------------------------------------------
//::
    
    
    
    
    
    
    


  
	// Funcion que convierte de infijo a Prefijo

    public static String infijoAPrefijo(String infijo) {
        // invierte la expresion de infijo
        String invertido = new StringBuilder(infijo).reverse().toString();
        
        // creamos una pila de operadores
        Stack<String> operador = new Stack<>();
        String[] c3d = invertido.split("\\s+"); //la expresion en prefija la corta y las metes en arreglos "\\s+"
        //String[] c3d = invertido.split("([A-Za-z1-9]\\w*)|([\\+\\-\\\\\\*\\)\\(])"); //la expresion en prefija la corta y las metes en arreglos "\\s+"
        
         
        
        // creamos un string builder para guardar la expresion prefijo
        StringBuilder prefijo = new StringBuilder();

        // iteramos atraves de la expresion infija invertida
      

        for (int i = 0; i < c3d.length; i++) {
            String ch = c3d[i];

            if (ch.equals(" ")) {
                continue; // Ignora los espacios
            }

            if (ch.matches("([A-Za-z0-9])\\w*")) {
                prefijo.append(ch).append(" "); // agrega el operando y el espacio
            } else if (ch.matches("\\)")) {
                operador.push(ch);
            } else if (ch.matches("\\(")) {
                while (!operador.isEmpty() && !operador.peek().matches("\\)")) {
                    prefijo.append(operador.pop()).append(" "); // agrega el operando y el espacio
                }
                operador.pop(); // sacamos el parentesis de cierre
            } else {
                while (!operador.isEmpty() && precedencia(operador.peek()) > precedencia(ch)) {
                    prefijo.append(operador.pop()).append(" "); // agregamos al operador y el espacio
                }
                operador.push(ch);
            }
        }

        while (!operador.isEmpty()) {
            prefijo.append(operador.pop()).append(" "); // agregamos el operador y el espacio
        }

        //  invierte el prefijo y lo regrese
        return prefijo.reverse().toString().trim();
    }

    public static int precedencia(String operador) {
        switch (operador) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    public static boolean esOperador(String operador) {
        switch (operador) {
            case "+":
            case "-":
            case "*":
            case "/":
                return true;
            default:
                return false;
        }
    }

	
   
        
     public String InfijoC3D(String infijo){
        
         String prefijo = infijoAPrefijo(infijo);//manda a llamar el metodo de infijo a prefijo y lo guarda en una variable
         emite(infijo); // emite la cadena de ejemplo a convertir
        String[] c3d = prefijo.split("\\s+"); //la expresion en prefija la corta y las metes en arreglos
         for(int i=0; c3d[i].equals(("t"+consecutivoTemp)) || i < c3d.length-1  ;i++)
         {

             if(c3d[i].matches("([\\+\\-\\*\\/\\^])\\w*") && c3d[i+1].matches("([A-Za-z0-9])\\w*")&& c3d[i+2].matches("([A-Za-z0-9])\\w*") )
             {
                
                 emite(tempnuevo() + " := " + c3d[i+1] + c3d[i] +  c3d[i+2]);//impirme el c3d 
                  c3d[i]="t"+(consecutivoTemp-1);            
                 for (int j = i+1; j < c3d.length-2; j++) {
                    
                         c3d[j]=c3d[j+2];  //recorre el arreglo            
                     }
                        
           
                  i=0;
            }
            
         }
         
        emite(tempnuevo() + " := " + c3d[1] + c3d[0] +  c3d[2]);  // imprime el ultimo c3d 
      
  
         return "t" + consecutivoTemp + "";//regresa la variable temporal ultima que se utilizo
     }
    
	//------------------------------------------------------------------------
}
