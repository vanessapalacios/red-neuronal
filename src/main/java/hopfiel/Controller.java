package hopfiel;

import Jama.Matrix;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Controller {
    @FXML // fx:id="canvas"
    private ImageView imgResult; // Value injected by FXMLLoader
    @FXML
    private GridPane gridPane; // Value injected by FXMLLoader
    GraphicsContext graphicsContext2D;
    @FXML
    private JFXButton btnsave;
    @FXML
    private JFXButton btnTraining;
    File dir;
    @FXML
    private ListView<Image> listImg;
    private ObservableList<Image> images;
    Matrix w;
    @FXML
    private JFXButton btnAssoc;
    private ArrayList<ArrayList<Double>> dataChars=new ArrayList<>();
    public static int SIZE_IMAGE=18;
    @FXML
    private JFXButton btnclear;

    private void onTrainingButton(){

        Matrix I=Matrix.identity(SIZE_IMAGE*SIZE_IMAGE,SIZE_IMAGE*SIZE_IMAGE);


        w = new Matrix(SIZE_IMAGE*SIZE_IMAGE,SIZE_IMAGE*SIZE_IMAGE);
        double [][] doublesMatrixTrainingInputs=new double[dataChars.size()][SIZE_IMAGE*SIZE_IMAGE];

        for (int i = 0; i < dataChars.size(); i++) {
            for (int j = 0; j < SIZE_IMAGE*SIZE_IMAGE; j++) {
                doublesMatrixTrainingInputs[i][j]=dataChars.get(i).get(j);
            }
        }


        Matrix m=new Matrix(doublesMatrixTrainingInputs);

        System.out.println(m.getColumnDimension()+" "+m.getRowDimension());
        m.print(2,0);

        Matrix wi;
        for (int i = 0; i < m.getRowDimension(); i++) {
            wi=m.getMatrix(i,i,0,m.getColumnDimension()-1).transpose().times(m.getMatrix(i,i,0,m.getColumnDimension()-1)).minus(I);
            w=wi.plus(w);
        }
        System.out.println(w.getColumnDimension()+" "+w.getRowDimension());


    }
    private void onSaveButton() {
           WritableImage img=gridPane.snapshot(new SnapshotParameters(),new WritableImage((int)gridPane.getWidth(),(int)gridPane.getHeight()));
           ArrayList<Double> temp=new ArrayList<>();
            gridPane.getChildren().forEach(n-> {
                if(n instanceof PaneCell) {
                    PaneCell p= (PaneCell) n;
                    temp.add(p.getColor().equals(Color.WHITE) ? -1d : 1d);
                    p.setStyle("-fx-background-color:#fff");
                    p.setColor(Color.WHITE);
                }

            });
        gridPane.setGridLinesVisible(true);
            dataChars.add(temp);

        System.out.println(temp.size());
//        int j=1;
//        for (int i = 0; i <SIZE_IMAGE*SIZE_IMAGE ; i++) {
//
//            if(j*18==i){
//                j++;
//                System.out.println();
//            }
//            System.out.print(temp.get(i).intValue()+"  ");
//        }

         File f=new File(dir.getAbsolutePath()+System.getProperty("file.separator")+"Img"+System.nanoTime()+".png");
           try {
               ImageIO.write(SwingFXUtils.fromFXImage(img,null),"png",f);
           } catch (IOException e1) {
               e1.printStackTrace();
           }
           System.out.println(f.getAbsolutePath());

           Image imageToList= null;
           try {
               imageToList = new Image(f.toURI().toURL().toString());
           } catch (MalformedURLException e1) {
               e1.printStackTrace();
           }
           images.add(imageToList);
            //graphicsContext2D.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

    }
    private void onClear(){

        gridPane.getChildren().forEach(n-> {
            if(n instanceof PaneCell) {
                PaneCell p= (PaneCell) n;
                p.setStyle("-fx-background-color:#fff");
                p.setColor(Color.WHITE);
            }

        });
        imgResult.setImage(null);

    }

    private void onAssoc() {
        double []t=new double[SIZE_IMAGE*SIZE_IMAGE];
        ArrayList<Double> temp=new ArrayList<>();
        gridPane.getChildren().forEach(n-> {
            if(n instanceof PaneCell) {
                PaneCell p= (PaneCell) n;
                temp.add(p.getColor().equals(Color.WHITE) ? -1d : 1d);
            }
        });

        for (int i = 0; i < temp.size(); i++) {
            t[i]=temp.get(i);

        }
        Matrix e=new Matrix(t,324);
        Matrix S=e.transpose().times(w);
        S=hard(S);
        S.print(2,0);
        Matrix t1,pattern = null;
        for (int i = 0; i < 100; i++) {
            t1=hard(S.times(w));

            if(compareTo(S,t1)){
                pattern=t1;
                break;
            }
            S=t1;
        }
        if(pattern==null){
            System.out.println("not match");
        }else{

            int row=0;
            for (int i = 0; i < dataChars.size(); i++) {
                for (int j = 0,k=0; j < dataChars.get(0).size(); j++) {
                    if(pattern.get(0,j)==dataChars.get(i).get(j)){
                       k++;
                    }
                    if(k==pattern.getColumnDimension()){
                       row=i;
                    }
                }
            }
           imgResult.setImage(listImg.getItems().get(row));
        }

    }
    private boolean compareTo(Matrix a,Matrix b){
        for (int i = 0; i < a.getRowDimension(); i++) {
            for (int j = 0; j < a.getColumnDimension(); j++) {
               if(a.get(i,j)!=b.get(i,j)){
                    return false;
                }
            }
        }
        return true;
    }

    private Matrix hard(Matrix x){

        for (int i = 0; i < SIZE_IMAGE*SIZE_IMAGE; i++) x.set(0, i, x.get(0, i) >= 0 ? 1 : -1);
    return x;
    }


    @FXML
    void initialize(){

        File f=new File(System.getProperty("user.home"));
        dir=new File(f,"DataHopfield");
        System.out.println(dir.delete());
        System.out.println(dir.mkdir());
        System.out.println(dir.getPath());

        btnAssoc.setOnMouseClicked(e->onAssoc());
        btnsave.setOnMouseClicked(e->onSaveButton());
        btnTraining.setOnMouseClicked(e->onTrainingButton());
        images=FXCollections.observableArrayList();
        listImg.setItems(images);
        btnclear.setOnMouseClicked(e->onClear());
        listImg.setCellFactory(l -> new ListCell<Image>() {

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty){
                    setText("");
                    setGraphic(null);
                }else{
                 setText("");
                    setGraphic(new ImageView(item));
                }
            }
        });

        for (int i = 0; i < SIZE_IMAGE; i++) {
            for (int j = 0; j < SIZE_IMAGE; j++) {
                PaneCell p = new PaneCell();
                p.setOnMouseMoved(event -> {
                    p.setStyle("-fx-background-color:#000");
                    p.setColor(Color.BLACK);
                });

                gridPane.add(p,j,i);
               // System.out.println(j+" "+i);
            }
        }
        gridPane.setGridLinesVisible(true);
        //gridPane.getChildren().forEach(e-> System.out.println(e.getClass()));
        //System.out.println(gridPane.getChildren().size());

    }



}
