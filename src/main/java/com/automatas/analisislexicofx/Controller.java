package com.automatas.analisislexicofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML Button btnBuscarArchivo,btnSalir,btnRevisar,btnTablaTokens;
    @FXML TextArea txtSalida;
    @FXML CheckBox chkArchivo;
    @FXML Label lblNombreArchivo;

    Stage ventanaPrincipal;
    Scanner leerArchivo = null;

    Alert alert = new Alert(Alert.AlertType.ERROR);

    FileChooser fileChooser = new FileChooser();
    File archivo = null;

    int NodoInicial = 0;
    int NodoActual;
    boolean Valida = true;

    // AGREGAR ESTADOS FINALES PARA PODER REALIZAR LA COMPROBACION
    Objeto[][] tkn_pal_res = new Objeto[64][64]; // Estados finales: 31, 8, 11, 17, 24, 30, 39, 50, 59, 63
    int[] tkn_pal_resFINALES = {31, 8, 11, 17, 24, 30, 39, 50, 59, 63};
    Objeto[][] tkn_id = new Objeto[2][2];
    int[] tkn_idFINALES = {0, 1};
    Objeto[][] tkn_cons = new Objeto[8][8];
    int[] tkn_consFINALES = {3, 7};
    Objeto[][] tkn_lit = new Objeto[3][3];
    int[] tkn_litFINALES = {2};
    Objeto[][] tkn_sep = new Objeto[2][2];
    int[] tkn_sepFINALES = {1};
    Objeto[][] tkn_term = new Objeto[2][2];
    int[] tkn_termFINALES = {1};
    Objeto[][] tkn_op_asig = new Objeto[3][3];
    int[] tkn_op_asigFINALES = {1, 2};
    Objeto[][] tkn_if = new Objeto[3][3];
    int[] tkn_ifFINALES = {2};
    Objeto[][] tkn_num = new Objeto[2][2];
    int[] tkn_numFINALES = {1};
    Objeto[][] tkn_lim = new Objeto[2][2];
    int[] tkn_limFINALES = {1};
    Objeto[][] tkn_op_arit = new Objeto[2][2];
    int[] tkn_op_aritFINALES = {1};
    Objeto[][] tkn_cor = new Objeto[2][2];
    int[] tkn_corFINALES = {1};

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        crearMatricesTokens();

        // Desactiva para el usuario el checkbox para el archivo y la caja de texto
        chkArchivo.setDisable(true);
        txtSalida.setEditable(false);

        // Acciones de los botones de la interfaz
        btnSalir.setOnAction(event -> System.exit(0));

        btnBuscarArchivo.setOnAction(event -> {

            fileChooser.setTitle("Buscar archivo");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivo de texto", "*.txt")
            );

            archivo = fileChooser.showOpenDialog(null);

            if ((archivo == null) || (archivo.getName().equals(""))) {
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("Archivo invalido o no seleccionado.");
                alert.showAndWait();
            }else{
                chkArchivo.setSelected(true);
                try {
                    lblNombreArchivo.setText(archivo.getName());
                    leerArchivo = new Scanner(archivo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });

        btnRevisar.setOnAction(event -> {

            // Evalua el archivo, si es que hay
            if(archivo != null){

                // Aqui va el metodo que revisa el archivo
                revisarArchivo(leerArchivo,txtSalida);

                chkArchivo.setSelected(false);
                lblNombreArchivo.setText(null);
                leerArchivo.close();
                archivo = null;
            }


        });

        btnTablaTokens.setOnAction(event -> muestraTablaTokens());

    }

    private boolean revisarCadena(String auxCadena, TextArea txtSalida, int numMatriz, int numLinea, int numPos){

        Objeto[][] matriz = null;
        int[] finales = null;
        String nombreToken = "";

        switch (numMatriz){
            case 1: matriz = tkn_pal_res; finales = tkn_pal_resFINALES; nombreToken = "tkn_pal_res"; break;
            case 2: matriz = tkn_cons; finales = tkn_consFINALES; nombreToken = "tkn_cons"; break;
            case 3: matriz = tkn_id; finales = tkn_idFINALES; nombreToken = "tkn_id"; break;
            case 4: matriz = tkn_lit; finales = tkn_litFINALES; nombreToken = "tkn_lit"; break;
            case 5: matriz = tkn_sep; finales = tkn_sepFINALES; nombreToken = "tkn_sep"; break;
            case 6: matriz = tkn_term; finales = tkn_termFINALES; nombreToken = "tkn_term"; break;
            case 7: matriz = tkn_op_asig; finales = tkn_op_asigFINALES; nombreToken = "tkn_op_asig"; break;
            case 8: matriz = tkn_if; finales = tkn_ifFINALES; nombreToken = "tkn_if"; break;
            case 9: matriz = tkn_num; finales = tkn_numFINALES; nombreToken = "tkn_num"; break;
            case 10: matriz = tkn_lim; finales = tkn_limFINALES; nombreToken = "tkn_lim"; break;
            case 11: matriz = tkn_op_arit; finales = tkn_op_aritFINALES; nombreToken = "tkn_op_arit"; break;
            case 12: matriz = tkn_cor; finales = tkn_corFINALES; nombreToken = "tkn_cor"; break;
        }

        Valida = true;
        char[] cadena = auxCadena.toCharArray();
        NodoActual = NodoInicial;

        for(int ContadorCharCadena = 0 ; ContadorCharCadena < cadena.length ; ContadorCharCadena++){

            for(int Columna = 0 ; Columna < matriz.length ; Columna++){

                if(matriz[NodoActual][Columna] == null){
                    continue;
                }

                txtSalida.insertText(txtSalida.getLength(),"\n\nNodo actual: q" + NodoActual);
                txtSalida.insertText(txtSalida.getLength(),"\nCaracter a evaluar: " + cadena[ContadorCharCadena]);

                if(Pattern.matches(matriz[NodoActual][Columna].validacion,String.valueOf(cadena[ContadorCharCadena]))){

                    txtSalida.insertText(txtSalida.getLength(),"\nValidacion correcta, cambia al nodo: q" + matriz[NodoActual][Columna].NodoDestino);
                    NodoActual = matriz[NodoActual][Columna].NodoDestino;
                    Valida = true;
                    break;

                }else {
                    txtSalida.insertText(txtSalida.getLength(),"\nNo hay validacion, buscando otro camino");
                    Valida = false;
                }
            }

            if(!Valida){
                break;
            }

        }

        for (int i = 0 ; i < finales.length ; i++){

            if(NodoActual == finales[i] && Valida){
                txtSalida.insertText(txtSalida.getLength(),"\n\nLEXEMA VALIDO [estado final de token encontrado]" +
                        "\nToken: " + nombreToken +
                        "\nLexema: " + auxCadena +
                        "\n# de linea: " + numLinea +
                        "\n# de columna: " + numPos);

                return true;
            }else {
                txtSalida.insertText(txtSalida.getLength(),"\n\nLexema no valido con estado final de token: " + nombreToken);
            }

        }
        return false;
    }

    private void revisarArchivo(Scanner leerArchivo, TextArea txtSalida){

        int numLinea = 1;
        int numPos;
        String auxLinea;
        StringBuilder auxCadena = new StringBuilder();

        // Mientras haya lineas en el archivo
        while (leerArchivo.hasNextLine()) {

            auxCadena.delete(0,auxCadena.capacity());
            //System.out.println(auxCadena);

            // Lee la siguiente linea y lo muestra en el area de texto
            auxLinea = leerArchivo.nextLine();
            numPos = 1;
            txtSalida.insertText(txtSalida.getLength(),"\n\n========== LINEA #" + numLinea + " ==========" + "\nContenido: " + auxLinea);
            numLinea++;

            if (auxLinea.equals("")){
                continue;
            }

            // Divide la linea por sus espacios y revisa cada cadena
            for (int i = 0 ; i < auxLinea.length() ; i++){

                numPos++;

                // Si el siguiente caracter NO es un espacio en blanco
                if (auxLinea.charAt(i) != ' '){
                    auxCadena.append(auxLinea.charAt(i));
                }else {

                    for (int k = 1 ; k <= 12 ; k ++){
                        txtSalida.insertText(txtSalida.getLength(),"\n\n---------- Lexema a revisar: " + auxCadena + " ----------");

                        if (revisarCadena(auxCadena.toString(),txtSalida,k,numLinea,numPos)){
                            break;
                        }
                    }
                    auxCadena.delete(0,auxCadena.capacity());
                }

            }
        }
    }


    // Crea las matrices de los automatas para validar los tokens
    private void crearMatricesTokens(){

        // 1 - Token de palabras reservadas
        // Estados finales: 31, 8, 11, 17, 24, 30, 39, 50, 59, 63
        tkn_pal_res[0][1] = new Objeto(0,1,"c");
        tkn_pal_res[1][2] = new Objeto(1,2,"l");
        tkn_pal_res[2][3] = new Objeto(2,3,"a");
        tkn_pal_res[3][4] = new Objeto(3,4,"s");
        tkn_pal_res[4][31] = new Objeto(4,31,"s");

        tkn_pal_res[0][5] = new Objeto(0,5,"m");
        tkn_pal_res[5][6] = new Objeto(5,6,"a");
        tkn_pal_res[6][7] = new Objeto(6,7,"i");
        tkn_pal_res[7][8] = new Objeto(7,8,"n");

        tkn_pal_res[0][9] = new Objeto(0,9,"n");
        tkn_pal_res[9][10] = new Objeto(9,10,"e");
        tkn_pal_res[10][11] = new Objeto(10,11,"w");

        tkn_pal_res[0][12] = new Objeto(0,12,"p");
        tkn_pal_res[12][13] = new Objeto(12,13,"u");
        tkn_pal_res[13][14] = new Objeto(13,14,"b");
        tkn_pal_res[14][15] = new Objeto(14,15,"l");
        tkn_pal_res[15][16] = new Objeto(15,16,"i");
        tkn_pal_res[16][17] = new Objeto(16,17,"c");

        tkn_pal_res[0][18] = new Objeto(0,18,"s");
        tkn_pal_res[18][19] = new Objeto(18,19,"t");
        tkn_pal_res[19][21] = new Objeto(19,21,"a");
        tkn_pal_res[21][22] = new Objeto(21,22,"t");
        tkn_pal_res[22][23] = new Objeto(22,23,"i");
        tkn_pal_res[23][24] = new Objeto(23,24,"c");

        tkn_pal_res[0][20] = new Objeto(0,20,"S");
        tkn_pal_res[20][25] = new Objeto(20,25,"c");
        tkn_pal_res[25][26] = new Objeto(25,26,"a");
        tkn_pal_res[26][27] = new Objeto(26,27,"n");
        tkn_pal_res[27][28] = new Objeto(27,28,"n");
        tkn_pal_res[28][29] = new Objeto(28,29,"e");
        tkn_pal_res[29][30] = new Objeto(29,30,"r");

        tkn_pal_res[20][32] = new Objeto(20,32,"y");
        tkn_pal_res[32][33] = new Objeto(32,33,"s");
        tkn_pal_res[33][34] = new Objeto(33,34,"t");
        tkn_pal_res[34][35] = new Objeto(34,35,"e");
        tkn_pal_res[35][36] = new Objeto(35,36,"m");
        tkn_pal_res[36][37] = new Objeto(36,37,".");

        tkn_pal_res[37][38] = new Objeto(37,38,"i");
        tkn_pal_res[38][39] = new Objeto(38,39,"n");

        tkn_pal_res[37][40] = new Objeto(37,40,"o");
        tkn_pal_res[40][41] = new Objeto(40,41,"u");
        tkn_pal_res[41][42] = new Objeto(41,42,"t");
        tkn_pal_res[42][43] = new Objeto(42,43,".");
        tkn_pal_res[43][44] = new Objeto(43,44,"p");
        tkn_pal_res[44][45] = new Objeto(44,45,"r");
        tkn_pal_res[45][46] = new Objeto(45,46,"i");
        tkn_pal_res[46][47] = new Objeto(46,47,"n");
        tkn_pal_res[47][48] = new Objeto(47,48,"t");
        tkn_pal_res[48][49] = new Objeto(48,49,"l");
        tkn_pal_res[49][50] = new Objeto(49,50,"n");

        tkn_pal_res[0][51] = new Objeto(0,51,"t");
        tkn_pal_res[51][52] = new Objeto(51,52,".");
        tkn_pal_res[52][53] = new Objeto(52,53,"n");
        tkn_pal_res[53][54] = new Objeto(53,54,"e");
        tkn_pal_res[54][55] = new Objeto(54,55,"x");
        tkn_pal_res[55][56] = new Objeto(55,56,"t");
        tkn_pal_res[56][57] = new Objeto(56,57,"I");
        tkn_pal_res[57][58] = new Objeto(57,58,"n");
        tkn_pal_res[58][59] = new Objeto(58,59,"t");

        tkn_pal_res[0][60] = new Objeto(0,60,"v");
        tkn_pal_res[60][61] = new Objeto(60,61,"o");
        tkn_pal_res[61][62] = new Objeto(61,62,"i");
        tkn_pal_res[62][63] = new Objeto(62,63,"d");


        // 2 - Token id
        // Estados finales: 0, 1
        tkn_id[0][0] = new Objeto(0,0,"[a-zA-Z]+");
        tkn_id[0][1] = new Objeto(0,1,"[a-zA-Z0-9]*");
        tkn_id[1][1] = new Objeto(1,1,"[a-zA-Z0-9]*");


        // 3 - Token constantes
        // Estados finales: 3, 7
        tkn_cons[0][1] = new Objeto(0,1,"i");
        tkn_cons[1][2] = new Objeto(1,2,"n");
        tkn_cons[2][3] = new Objeto(2,3,"t");

        tkn_cons[0][4] = new Objeto(0,4,"l");
        tkn_cons[4][5] = new Objeto(4,5,"o");
        tkn_cons[5][6] = new Objeto(5,6,"n");
        tkn_cons[5][7] = new Objeto(5,7,"g");

        // 4 - Token lit
        // Estados finales: 2
        tkn_lit[0][1] = new Objeto(0,1,"\"");
        tkn_lit[1][1] = new Objeto(1,1,"[^\\\"]*");
        tkn_lit[1][2] = new Objeto(1,2,"\"");

        // 5 - Token sep
        // Estados finales: 1
        tkn_sep[0][1] = new Objeto(0,1,"[()]?");

        // 6 - Token term
        // Estados finales: 1
        tkn_term[0][1] = new Objeto(0,1,";");

        // 7 - Token op_asig
        // Estados finales: 1, 2
        tkn_op_asig[0][1] = new Objeto(0,1,"=");
        tkn_op_asig[0][2] = new Objeto(0,2,"[<>]?");

        // 8 - Token if
        // Estados finales: 2
        tkn_if[0][1] = new Objeto(0,1,"i");
        tkn_if[1][2] = new Objeto(1,2,"f");

        // 9 - Token num
        // Estados finales: 1
        tkn_num[0][1] = new Objeto(0,1,"[0-9]?");
        tkn_num[1][1] = new Objeto(1,1,"[0-9]+");

        // 10 - Token lim
        // Estados finales: 1
        tkn_lim[0][1] = new Objeto(0,1,"[{}]?");

        // 11 - Token op_arit
        // Estados finales: 1
        tkn_op_arit[0][1] = new Objeto(0,1,"[+-\\\\*/]?");

        // 12 - Token cor
        // Estados finales: 1
        tkn_cor[0][1] = new Objeto(0,1,"[\\]\\[]?");

    }
    // Muestra la ventana que contiene la imagen de la tabla de tokens
    private void muestraTablaTokens(){

        try {

            ocultarActual();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ventanaTablaTokens.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Mostrando tabla de tokens");
            stage.setScene(new Scene(root1));
            stage.showAndWait();

            mostrarActual();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // Metodos para ocultar / mostrar la ventana principal, se usan cuando se abre la ventana de procesar //
    public void ocultarActual() {
        ventanaPrincipal = (Stage) chkArchivo.getScene().getWindow();
        ventanaPrincipal.hide();
    }
    public void mostrarActual() {
        ventanaPrincipal = (Stage) chkArchivo.getScene().getWindow();
        ventanaPrincipal.show();
    }


}