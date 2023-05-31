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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class GenCodigoInt {
    private Compilador cmp;
    private int consecutivoTemp = 1;
    private int consecutivoEtiq = 1;
    public static final int NIL = 0;
    
    private String preAnalisis;
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
        preAnalisis = cmp.be.preAnalisis.complex;
        programa();
    }    

    //--------------------------------------------------------------------------

    private void emite ( String c3d ) {
        cmp.iuListener.mostrarCodInt( c3d );
    }
    //--------------------------------------------------------------------------
    //************EMPAREJAR**************//
    private void emparejar ( String t ) {
	if (cmp.be.preAnalisis.complex.equals(t)){
            cmp.be.siguiente ();
            preAnalisis = cmp.be.preAnalisis.complex;
        }
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
     
    private static int prioridades(char operador) {
        switch (operador) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
    
    public static ArrayList<String> infijoAPrefijo(String infijo) {
        String[] tokens = infijo.split(" ");
        
        Stack<String> pila = new Stack<>();
        
        ArrayList<String> prefijo = new ArrayList<>();
        
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            if (esOperador(token.charAt(0))) {
                while ( !pila.isEmpty() && (prioridades(pila.peek().charAt(0)) > prioridades(token.charAt(0))) )
                    prefijo.add( pila.pop() );
                pila.push(token);
            } 
            else if (token.equals(")"))
                pila.push(token);
            else if (token.equals("(")){
                while (!pila.isEmpty() && !pila.peek().equals(")"))
                    prefijo.add(pila.pop());
                pila.pop(); 
            } 
            else
                prefijo.add(token);
        }
        
        while (!pila.isEmpty()) {
            prefijo.add(pila.pop());
        }
        
        ArrayList <String> prefijoNuevo = new ArrayList <> ();
        for ( int i = prefijo.size()-1; i >= 0; i-- )
            prefijoNuevo.add( prefijo.get(i) );
        return prefijoNuevo;
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

    public static boolean esOperador(char operador) {
        switch (operador) {
            case '+':
            case '-':
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }
    
    private ArrayList <String> nuevoArreglo ( ArrayList <String> entrada, int posicion, String temporal ) {
        ArrayList <String> salida = new ArrayList<String>();
        
        for ( int i = 0; i < entrada.size(); i++ ) {
            if ( i != posicion && i != ( posicion + 1 ) && i != ( posicion + 2 ) ) {
                salida.add( entrada.get( i ) );
            } else if ( i == posicion ) {
                salida.add( temporal );
            }
        }
        
        return salida;
    }
    
     public String InfijoC3D(ArrayList <String> expresionArray){
        int i = 0;
        String ultimoTemporal = "";
        
        while ( expresionArray.size() >= 3 ){
            String c = expresionArray.get( i );
            
            if ( this.esOperador(c.charAt( 0 )) && 
                Character.isLetterOrDigit( expresionArray.get( i + 1 ).charAt( 0 ))  &&
                Character.isLetterOrDigit( expresionArray.get( i + 2 ).charAt( 0 )) ){
                String actT = tempnuevo();
                
                emite ( actT + ":=" + expresionArray.get( i + 1 ) + c + expresionArray.get( i + 2 ) );
                
                expresionArray = nuevoArreglo( expresionArray, i, actT );
                
                i=0;
                ultimoTemporal = actT;
            } else 
                i++;
        }
        
        return ultimoTemporal;
     }
    
	//------------------------------------------------------------------------
    private void programa () {
        Atributo declaraciones = new Atributo();
        Atributo declaraciones_subprogramas = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if ( preAnalisis.equals ( "dim"      ) ||
             preAnalisis.equals ( "function" ) ||
             preAnalisis.equals ( "sub"      ) ||
             preAnalisis.equals ( "id"       ) ||
             preAnalisis.equals ( "if"       ) ||
             preAnalisis.equals ( "call"     ) ||  
             preAnalisis.equals ( "do"       ) ||  
             preAnalisis.equals ( "end"      ) ) {
            
            // programa -> declaraciones declaraciones_subprogramas proposiciones_optativas end 
            declaraciones ( declaraciones );
            declaraciones_subprogramas ( declaraciones_subprogramas );
            proposiciones_optativas ( proposiciones_optativas );
            emparejar ( "end" );
            
        } else {
            error ( "[programa]: Inicio incorrecto de programa." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
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
        }else {
            // lista_declaraciones_prima -> empty 
        }
    }
    
    private void proposicion_prima(Atributo proposicion_prima) {
        //proposicion’ → ( lista_expresiones ){7}
        Atributo lista_expresiones = new Atributo();
        if(preAnalisis.equals('(')) {
            emparejar("(");
            lista_expresiones(lista_expresiones);
            emparejar(")");
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
        }
        else {
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
        }
        else{ 
        }
    }
    
    private void condicion(Atributo condicion) {
        Atributo expresion2 = new Atributo();
        Atributo expresion3 = new Atributo();
        Linea_BE oprel = new Linea_BE();
        
        String terminales[] = {"id", "num", "num.num", "(", "literal"};
        if(estaEn(terminales)){
            expresion(expresion2);
            oprel = cmp.be.preAnalisis;
            emparejar("oprel");
            expresion(expresion3);
            //Acción Semántica 8
            String tempExpr1 = this.InfijoC3D(infijoAPrefijo(expresion2.valor) );
            String tempExpr2 = this.InfijoC3D(infijoAPrefijo(expresion3.valor) );
            
            emite("if " + (!tempExpr1.equals ("") ? tempExpr1 + " " : expresion2.valor) + oprel.lexema + " " + 
                  (!tempExpr2.equals ("") ? tempExpr2 + " " : expresion2.valor ) + "goto " + condicion.verdadera);
            emite("goto " + condicion.falsa);
            emite(condicion.verdadera + ":");
            
        }else{
            error(String.format("syntax error in line %s: Condición no válida",
                    cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void expresion(Atributo expresion){
        //expresion → termino{14}  expresion’{15} |  
        Atributo termino = new Atributo();
        Atributo expresion_prima = new Atributo();
        Linea_BE literal = new Linea_BE();
        
        String terminales[] = {"id", "num", "num.num", "("};
        if(estaEn(terminales)){
            termino(termino);
            expresion_prima(expresion_prima);
            //Acción Semántica 9
            expresion.valor = termino.valor + expresion_prima.valor ;
            
        }else if(preAnalisis.equals("literal")){
            literal = cmp.be.preAnalisis;
            emparejar("literal");
            
            //Acción Semántica 10
            expresion.valor = literal.lexema;
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
            
            //Acción Semántica 11
            expresion_prima.valor = "+ " + termino.valor + expresion_prima2.valor;
        }else{
            //Acción Semántica 12
            expresion_prima.valor = "";
        }
    }
    
    private void termino(Atributo termino) {
        //termino → factor{20} termino’ {21}
        Atributo factor = new Atributo();
        Atributo termino_prima = new Atributo();
        
        String terminales[] = {"id", "num", "num.num", "("};
        if(estaEn(terminales)){
            factor(factor);
            termino_prima(termino_prima);
            
            //Acción Semántica 13
            termino.valor = factor.valor + termino_prima.valor;
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
            termino_prima(termino_prima2);
            
            //Acción Semántica 14
            termino_prima.valor = "* " + factor.valor + termino_prima2.valor;
        }
        else{
            //Acción Semántica 15
            termino_prima.valor = "";
        }
    }
    
    private void factor(Atributo factor) {
        //factor → id  factor’{25} | num{26} | num.num{27}  |  ( expresion ){28}
        Linea_BE id = new Linea_BE();
        Linea_BE num = new Linea_BE();
        Linea_BE numnum = new Linea_BE();
        Atributo factor_prima = new Atributo();
        Atributo expresion = new Atributo();
        
        if(preAnalisis.equals("id")){
            id = cmp.be.preAnalisis;
            emparejar("id");
            //Acción Semántica 16
            factor.valor = id.lexema + " ";
            
            factor_prima(factor_prima);
        } else if (preAnalisis.equals("num")) {
            num = cmp.be.preAnalisis;
            emparejar("num");
            //Acción Semántica 17
            factor.valor = num.lexema + " ";
            
        } else if (preAnalisis.equals("num.num")) {
            numnum = cmp.be.preAnalisis;
            emparejar("num.num");
            //Acción Semántica 18
            factor.valor = numnum.lexema + " ";
            
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            expresion(expresion);
            emparejar(")");
            //Acción Semántica 19
            factor.valor = "( " + expresion.valor + ") ";
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
        }else{ 
        }
    }
    
    private void tipo(Atributo tipo) {
        //tipo → integer{31}  | single{32}  | string{33}
        if(preAnalisis.equals("integer")) {
            emparejar("integer");
        }else if(preAnalisis.equals("single")) {
            emparejar("single");
        }else if (preAnalisis.equals("string")) {
            emparejar("string");
        }else {
            error(String.format("syntax error in line %s: Tipo de dato inválido",
                    cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void declaraciones_subprogramas(Atributo declaraciones_subprogramas) {
        Atributo declaracion_subprograma = new Atributo();
        Atributo declaraciones_subprogramas2 = new Atributo();
        
        String terminales[] = {"function", "sub"};
        if(estaEn(terminales)){
            declaracion_subprograma(declaracion_subprograma);
            declaraciones_subprogramas(declaraciones_subprogramas2);
        }
    }
    
    private void declaracion_subprograma(Atributo declaracion_subprograma){
        //declaracion_subprograma → declaracion_funcion{36}  |  declaracion_subrutina{37}
        Atributo declaracion_funcion = new Atributo();
        Atributo declaracion_subrutina = new Atributo();
        if(preAnalisis.equals("function")){
            declaracion_funcion(declaracion_funcion);
        }else if(preAnalisis.equals("sub")){
            declaracion_subrutina(declaracion_subrutina);
            
        }else{
            error(String.format("syntax error in line %s: Mala declaración de subrprograma",
                    cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void declaracion_funcion(Atributo declaracion_funcion) {
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
            emparejar("end");
            emparejar("function");
        }else{
            error(String.format("syntax error in line %s: Mala declaración de función",
                    cmp.be.preAnalisis.numLinea));
        }

    }
    
    private void declaracion_subrutina(Atributo declaracion_subrutina) {
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if(preAnalisis.equals("sub")) {
            emparejar("sub");
            id = cmp.be.preAnalisis;
            emparejar("id");
            argumentos(argumentos);
            proposiciones_optativas(proposiciones_optativas);
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
        }else{ 
            
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
            
            //Acción Semántica 1
            if(!proposiciones_optativas.siguiente.equals("")){
                emite("goto " + proposiciones_optativas.siguiente);
            }
        }else{ 
        }
    }
    
    private void proposicion(Atributo proposicion) {
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
            //Acción Semántica 2
            String temporal;
            try {
                temporal = this.InfijoC3D(infijoAPrefijo(expresion.valor) ); 
            } catch ( Exception ex ) {
                temporal = "";
            }
        
            if (!temporal.equals(""))
                emite( id.lexema + " := " + temporal );
            else 
                emite( id.lexema + " := " + expresion.valor );
            }
        else if (preAnalisis.equals("call")) {
            emparejar("call");
            id = cmp.be.preAnalisis;
            emparejar("id");
            proposicion_prima(proposicion_prima);
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            //Acción Semántica 3
            proposicion.siguiente = tempnuevo();
            condicion.verdadera = tempnuevo();
            condicion.falsa = tempnuevo();
            proposiciones_optativas2.siguiente = proposicion.siguiente;
            
            condicion(condicion);
            emparejar("then");
            proposiciones_optativas(proposiciones_optativas2);
            //Acción Semántica 4
            emite( condicion.falsa + ":" );
            
            emparejar("else");
            proposiciones_optativas(proposiciones_optativas3);
            
            //Acción Semántica 5
            emite( proposicion.siguiente + ":" );
            emparejar("end");
            emparejar("if");
        }
        else if (preAnalisis.equals("do")) {
            emparejar("do");
            emparejar("while");
            
            //Acción Semántica 6
            proposicion.comienzo = tempnuevo();
            proposicion.siguiente = tempnuevo();
            condicion2.verdadera = tempnuevo();
            condicion2.falsa = proposicion.siguiente;
            proposiciones_optativas3.siguiente = proposicion.comienzo;
            emite( proposicion.comienzo +  ":" );
            
            condicion(condicion2);
            proposiciones_optativas(proposiciones_optativas4);
            //Acción Semántica 7
            emite( "goto " + proposicion.comienzo );
            emite( condicion2.falsa + ":" );
            
            emparejar("loop");
        } else{
            error(String.format("syntax error in line %s: Expresión inválida",
                    cmp.be.preAnalisis.numLinea));
        }
    }
    
    private void propsicion_prima(Atributo proposicion_prima) {
        Atributo lista_expresiones = new Atributo();
        if(preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones(lista_expresiones);
            emparejar(")");
        //empty
        }
    }
}
