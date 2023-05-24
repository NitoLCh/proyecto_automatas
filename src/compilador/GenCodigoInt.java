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
import java.util.Stack;


public class GenCodigoInt {
  public static final int NIL = 0;
    private Compilador cmp;
    private int consecutivoTemp;
    
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
                else if(!expresion.tipo.equals("")) 
                    if((cmp.ts.buscaTipo(id.entrada).equals("SINGLE") && expresion.tipo.equals("INTEGER")) ||
                        (cmp.ts.buscaTipo(id.entrada).equals("INTEGER") && expresion.tipo.equals("SINGLE")) ||
                        (cmp.ts.buscaTipo(id.entrada).equals("INTEGER") && getRango(expresion.tipo).equals("SIBGLE")) ||
                        (cmp.ts.buscaTipo(id.entrada).equals("SINGLE") && getRango(expresion.tipo).equals("INTEGER"))){
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
