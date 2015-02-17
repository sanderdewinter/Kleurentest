package com.robinsiep;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import java.awt.*;
import java.util.*;
import java.util.List;


public class SWTApp {

    private Shell shell;
    public static Button start, stop;
    public static Text input;
    public static CLabel textLabel, paginaNummer;
    public static Image image;
    public static Label label;
    private static List<String> images = new ArrayList<String>();
    private static List<Integer> answers = new ArrayList<Integer>();
    private static List<Boolean> right = new ArrayList<Boolean>();
    private static int pagina = 0;
    private boolean running = false;
    int xcenter;
    int ycenter;


    public SWTApp(Display display) {
        shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.BORDER |
                SWT.APPLICATION_MODAL | SWT.MIN);
        shell.setText("Kleurenblind test");
        final Display screen = display;
        drawScreen(screen);
        initUI();

        shell.setSize(500, 500);
        center(shell);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        

    }

    public void drawScreen(Display display){
        final Display screen = display;
        shell.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                Rectangle clientArea = shell.getClientArea();
                int width = clientArea.width;
                int height = clientArea.height;
                e.gc.setClipping(20,20,width - 40, height - 130);
                e.gc.setBackground(screen.getSystemColor(SWT.COLOR_BLACK));
                e.gc.setLineWidth(5);
                e.gc.drawLine(25,25,width-30,25);
                e.gc.drawLine(width-30,25,width-30,height - 115);
                e.gc.drawLine(width-30,height-115,25,height-115);
                e.gc.drawLine(25,height-115,25,25);
            }
        });
    }

    public void center(Shell shell) {

        Rectangle bds = shell.getDisplay().getBounds();

        Point p = shell.getSize();

        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;

        shell.setBounds(nLeft, nTop, p.x, p.y);
    }

    void getImage(int links){
        Device dev = shell.getDisplay();
        image = new Image(dev, images.get(links));
        //image
    }

    public void initUI() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);
        //image layout
        final Group groupImg = new Group(shell, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        groupImg.setLayout(gridLayout);
        groupImg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        groupImg.setText("Image");
        paginaNummer = new CLabel(groupImg,SWT.BORDER);
        label = new Label(groupImg,SWT.FILL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));



        //controls layout

        Group groupControl = new Group(shell, SWT.NONE);
        groupControl.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        groupControl.setLayout(gridLayout);
        textLabel = new CLabel(groupControl,SWT.NONE);
        input = new Text(groupControl,SWT.BORDER | SWT.CENTER);
        stop = new Button(groupControl, SWT.PUSH);
        start = new Button(groupControl, SWT.PUSH);


        //set texts
        textLabel.setText("Klik op start om te beginnen.");
        start.setText("Start");

        //set invisibility
        stop.setVisible(false);
        input.setVisible(false);
        paginaNummer.setVisible(false);

        //set control layouts
        GridData defaultGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
        defaultGridData.grabExcessHorizontalSpace = true;
        textLabel.setLayoutData(defaultGridData);
        //pack everything

        groupImg.pack();
        groupControl.pack();

        //set listeners
        stop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeState(false);
                pagina = 1;
                running = false;
                System.out.println(pagina);
            }
        });
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (running == true) {
                    pagina += 1;
                    paginaNummer.setText("Pagina:" + pagina + "/" + images.size());
                    checkInput();
                    nextPage();

                } else if (running == false) {
                    makeList();
                    changeState(true);
                    right.clear();
                    pagina = 1;
                    getImage(pagina-1);
                    label.setImage(image);
                    paginaNummer.setText("Pagina:"+pagina+"/" + images.size());
                }
            }
        });

    }

    private void checkInput(){
        if(input.getText().isEmpty()){
            textLabel.setText("Fout, probeer wel een poging te doen");
            System.out.println("log");
            right.add(false);
        }
        else if(Integer.parseInt(input.getText()) == answers.get(pagina-2)){
            textLabel.setText("Goed");
            right.add(true);
        } else {
            textLabel.setText("Fout");
            right.add(false);
        }
    }

    private void nextPage(){
        if(pagina <=images.size()){
            getImage(pagina-1);
            label.setImage(image);
            input.setText("");
        }  else {
            System.out.println("the end");
            changeState(false);
        }
    }

    public void changeState(Boolean state) {
        running = state;
        input.setVisible(state);
        stop.setVisible(state);
        label.setVisible(state);
        paginaNummer.setVisible(state);
        input.setText("");
        if (state) {
            start.setText("submit");
            stop.setText("Stop");
            textLabel.setText("Voer hiernaast het getal in dat u ziet in het plaatje");
            System.out.println("if");
        }else{
            System.out.println(right.size());
            System.out.println(images.size());
            if(right.size() == images.size()){
                System.out.println("Else");
                int rightCount = 0;
                for(int i=0; i < right.size(); i++){
                    if (right.get(i))
                    rightCount++;
                }
                float goodPerc = rightCount / right.size();
                System.out.println(goodPerc);
                if(goodPerc >= 0.75){
                    textLabel.setText("U heeft geen last van kleurenblindheid. Uw heeft " + rightCount + " van de " + right.size() +  " goed");
                }else{
                    textLabel.setText("U heeft kleurenblindheid.Uw heeft " + rightCount + " van de " + right.size() + " goed");
                }
                right.clear();
            }else{
                textLabel.setText("Klik op de start knop om te beginnen met de test");
            }
            start.setText("start");

        }
    }


    public static void main(String[] args) {
        Display display = new Display();
        makeList();
        new SWTApp(display);
        display.dispose();
    }

    private static void makeList(){
        images.clear();
        answers.clear();
        images.add("resources/images/img1.jpg");
        answers.add(56);
        images.add("resources/images/img2.jpg");
        answers.add(8);
        images.add("resources/images/img3.jpg");
        answers.add(15);
        images.add("resources/images/img4.jpg");
        answers.add(74);

        //randomize both arrays together
        long seed = System.nanoTime();
        Collections.shuffle(images, new Random(seed));
        Collections.shuffle(answers, new Random(seed));
    }
}
