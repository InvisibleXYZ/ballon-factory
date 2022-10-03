import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Visual Layer
public class bf extends JFrame{
    private final Storage storage = new Storage();
    private final List<Factory> factoryList = new ArrayList<>();
    private final List<Transporter> transporterList = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                bf::new
        );
    }

    public JPanel createFactory(JPanel factories){
        JPanel factoryPanel = new JPanel();
        factoryPanel.setLayout(new BoxLayout(factoryPanel, BoxLayout.Y_AXIS));
        factoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        factoryPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        factoryPanel.setPreferredSize(new Dimension(200,200));

        JLabel factoryLabel = new JLabel("Factory");
        Factory factory = new Factory(storage);
        factoryList.add(factory);
        JLabel producedLabel = new JLabel("Produced: 0");
        Thread producedThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                producedLabel.setText("Produced: " + factory.getProduced());
                try {
                    Thread.sleep(1000/30);
                } catch (InterruptedException e) {
                    factory.interrupt();
                    Thread.currentThread().interrupt();
                }
            }
        });
        producedThread.start();

        factoryPanel.add(factoryLabel);
        factoryPanel.add(producedLabel);

        JPanel sliderValuePanel = new JPanel();
        JSlider slider = new JSlider();
        slider.setMinimum(100);
        slider.setMaximum(3000);
        slider.setValue(factory.getFrequency());
        JLabel value = new JLabel(slider.getValue() + "");
        slider.addChangeListener((e) -> {
            value.setText(slider.getValue() + "");
            factory.setFrequency(slider.getValue());
        });
        sliderValuePanel.add(slider);
        sliderValuePanel.add(value);
        factoryPanel.add(sliderValuePanel);

        JButton deleteButton = new JButton("Delete factory");
        deleteButton.addActionListener(e -> {
            factoryList.remove(factory);
            factories.remove(factoryPanel);
            producedThread.interrupt();
            factory.interrupt();
            this.pack();
            this.repaint();
        });
        factoryPanel.add(deleteButton);

        return factoryPanel;
    }

    private synchronized JPanel createTransporter() {
        JPanel transporterPanel = new JPanel();
        transporterPanel.setLayout(new BoxLayout(transporterPanel, BoxLayout.Y_AXIS));
        transporterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        transporterPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel transporterLabel = new JLabel("Transporter");
        transporterPanel.add(transporterLabel);

        Transporter transporter = new Transporter(storage);
        transporterList.add(transporter);
        JLabel statusLabel = new JLabel(transporter.getStatus());
        Thread statusThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                statusLabel.setText(transporter.getStatus());
                this.repaint();
                try {
                    Thread.sleep(1000/30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        statusThread.start();
        transporterPanel.add(statusLabel);

        JButton startStopButton = new JButton("Stop");
        transporterPanel.add(startStopButton);
        transporterPanel.add(Box.createRigidArea(new Dimension(50,0)));
        startStopButton.addActionListener(e -> {
                if (startStopButton.getText().equals("Stop")) {
                    startStopButton.setText("Start");
                    transporter.pauseThread();
                    transporter.setStatus("Stopped");
                }
                else {
                    startStopButton.setText("Stop");
                    transporter.resumeThread();
                }
            });

        return transporterPanel;
    }

    public bf(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel factories = new JPanel();
        factories.setLayout(new BoxLayout(factories, BoxLayout.X_AXIS));
        factories.setAlignmentX(Component.CENTER_ALIGNMENT);
        factories.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel createButtonPanel = new JPanel();
        createButtonPanel.setLayout(new BoxLayout(createButtonPanel, BoxLayout.X_AXIS));
        createButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButtonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        JButton createButton = new JButton("Create factory");
        createButton.addActionListener(el-> {
            factories.add(createFactory(factories));
            factories.remove(createButton);
            factories.add(createButton);
            this.pack();
        });
        createButtonPanel.add(createButton);
        factories.add(createButtonPanel);

        JScrollPane scrollFactories = new JScrollPane(factories, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollFactories.setPreferredSize(new Dimension(450, 110));

        JPanel storagePanel = new JPanel();
        storagePanel.setLayout(new GridLayout(10,2));
        storagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        storagePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        storagePanel.setPreferredSize(new Dimension(500, 300));
        List<JLabel> storageLabels = new ArrayList<>();
        List<JPanel> colorPanelList = new ArrayList<>();
        for(int i=0; i<storage.getStorage().length;i++){
            storageLabels.add(new JLabel("empty"));
            colorPanelList.add(new JPanel());
        }

        Thread storageThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                Balloon[] copyStorage = storage.getStorage();
                for(int i = 0; i < copyStorage.length; i++){
                    if(copyStorage[i]==null){
                        storageLabels.get(i).setText(" empty ");
                        colorPanelList.get(i).setBackground(null);
                    }
                    else{
                        storageLabels.get(i).setText(copyStorage[i].getNumber()+"");
                        try {
                            colorPanelList.get(i).setBackground(
                                    copyStorage[i].getColor());
                        }catch (NullPointerException e){
                            colorPanelList.get(i).setBackground(null);
                        }

                    }
                }
            }
        });

        storageThread.start();

        for(int i =0; i<storageLabels.size(); i++){
            JPanel storageColorPanel = new JPanel();
            storageColorPanel.add(storageLabels.get(i));
            storageColorPanel.add(colorPanelList.get(i));

            storagePanel.add(storageColorPanel);
            this.pack();
        }

        JPanel transporters = new JPanel();
        transporters.setLayout(new BoxLayout(transporters, BoxLayout.X_AXIS));
        transporters.setAlignmentX(Component.CENTER_ALIGNMENT);
        transporters.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel createTransporterPanel = new JPanel();
        createTransporterPanel.setLayout(new BoxLayout(createTransporterPanel, BoxLayout.X_AXIS));
        createTransporterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createTransporterPanel.setAlignmentY(Component.CENTER_ALIGNMENT);


        JButton createTransporterButton = new JButton("Create transporter");
        createTransporterButton.addActionListener(e -> {
            transporters.add(createTransporter());
            transporters.remove(createTransporterButton);
            transporters.add(createTransporterButton);
            this.pack();
            this.repaint();
        });
        createTransporterPanel.add(createTransporterButton);

        transporters.add(createTransporterPanel);

        JScrollPane scrollTransporters = new JScrollPane(transporters, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollTransporters.setPreferredSize(new Dimension(450, 110));

        mainPanel.add(scrollFactories);
        mainPanel.add(storagePanel);
        mainPanel.add(scrollTransporters);

        Thread emergencyThread = new Thread(() -> {
            EmergencyCanvas emergencyCanvas = new EmergencyCanvas(this);
            emergencyCanvas.setPreferredSize(new Dimension(1400,300));
            while (!Thread.currentThread().isInterrupted()){
                if(storage.getStorage()[storage.getStorage().length - 10] != null) {
                    for (Factory e : factoryList) {
                        e.pauseThread();
                    }
                    for (Transporter e : transporterList) {
                        e.pauseThread();
                    }

                    this.remove(mainPanel);
                    this.add(emergencyCanvas);
                    this.pack();

                    Arrays.fill(storage.getStorage(), null);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    this.remove(emergencyCanvas);
                    this.add(mainPanel);
                    this.pack();

                    for (Factory e : factoryList) {
                        e.resumeThread();
                    }
                    for (Transporter e : transporterList) {
                        if(!e.getStatus().equals("Stopped")){
                            e.resumeThread();
                        }
                    }
                }
                try {
                    Thread.sleep(1000/30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        emergencyThread.start();

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Balloon Factory");
    }
}

class EmergencyCanvas extends Canvas implements Runnable {
    private final JFrame frame;
    private final List<Balloon> ballList = new ArrayList<>();

    public EmergencyCanvas(JFrame frame) {
        this.frame = frame;
        for(int i =0; i < 100; i++){
            ballList.add(
                    new Balloon(
                            BalloonColor.get((int)(Math.random() * BalloonColor.values().length)),
                            (int)(Math.random() * (frame.getWidth()*10)),
                            (int)(Math.random() * (frame.getHeight()+frame.getHeight())), frame)
            );
        }
        new Thread(this).start();
    }

    public void update(Graphics g){
        Image image = createImage(getWidth(), getHeight());
        Graphics imageG = image.getGraphics();

        for (Balloon ball : ballList){
            ball.draw(imageG);
        }
        imageG.setColor(Color.red);
        imageG.setFont(new Font(this.getFont().getName(), Font.BOLD, 30));
        imageG.drawString("EMERGENCY RELEASE", (int)(frame.getWidth()/2.5), frame.getHeight()/2);

        g.drawImage(image, 0, 0, this);
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            repaint();
            try {
                Thread.sleep(1000/30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

enum BalloonColor {
    white(255,255,255), black(0,0,0), red(255,0,0),
    blue(0,0, 255), yellow(250,218,94), gray(220,220,220),
    green(0, 255, 0), purple(230, 230, 250), pink(255, 192, 203),
    orange(255,140,0), brown(255,248,220), cyan(0, 255, 255),
    violet(230,230,250), darkGreen(0, 150, 0), darkRed(150,0,0),
    darkBlue(0,0,150);

    public final int r, g, b;

    BalloonColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private static final BalloonColor[] colors = new BalloonColor[16];

    static {
        int i=0;
        for(BalloonColor e : values()){
            colors[i++]=e;
        }
    }

    public static BalloonColor get(int num){
        return colors[num];
    }
}

//Logic Layer ----------------------------------------------------------------------------------------------------------
class Balloon extends Thread {
    private long number;
    private static long largestNumber;

    private int x, y;

    private final int height = 100;
    private final Color color;
    private JFrame frame;

    public Balloon(BalloonColor color, int x, int y, JFrame frame) {
        this.x = x;
        this.y = y;
        this.color = new Color(color.r, color.g, color.b);
        this.frame = frame;
        start();
    }

    public Balloon(BalloonColor color) {
        number = largestNumber++;
        this.color = new Color(color.r, color.g, color.b);
    }

    public long getNumber() {
        return number;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            move();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    private void move(){
        if(y < -height){
            y = (int)(Math.random() * frame.getHeight()) + frame.getHeight();
        }
        else {
            y -= 5;
        }
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, 80, height);
    }
}

class Factory extends Thread {
    private int frequency = 1000;
    private final Storage storage;
    private int produced;

    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public Factory(Storage storage) {
        start();
        this.storage = storage;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            synchronized (pauseLock) {
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
            if (storage.getStorage()[storage.getStorage().length-1] == null) {
                Balloon balloon = new Balloon(BalloonColor.get((int)(Math.random() * BalloonColor.values().length)));
                storage.add(balloon);
                produced++;
            }
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public int getProduced() {
        return produced;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}

class Storage {
    private final Balloon[] storage = new Balloon[99];

    public Balloon takeFirst() {
        Balloon toReturn = storage[0];
        Balloon[] tmpStorage = storage;
        for(int i=1; i<storage.length; i++){
            if(i != storage.length - 1) {
                storage[i - 1] = tmpStorage[i];
            }
            else{
                storage[i-2] = tmpStorage[i-1]; //Prevent the bug
                storage[i] = null;
            }
        }
        return toReturn;
    }

    public void add(Balloon balloon){
        for(int i = 0; i<storage.length; i++){
            if(storage[i] == null){
                storage[i] = balloon;
                return;
            }
        }
    }

    public Balloon[] getStorage() {
        return storage;
    }
}

class Transporter extends Thread {
    private final Balloon[] transport = new Balloon[10];
    private final Storage storage;
    private String status = "None";

    private boolean paused = false;
    private final Object pauseLock = new Object();

    public Transporter(Storage storage) {
        this.storage = storage;
        start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (pauseLock) {
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
            for (int i = 0; i < transport.length; i++) {
                if(paused) break;

                if (transport[i] == null && storage.getStorage()[0] != null) {
                    status = "Loading";
                    transport[i] = storage.takeFirst();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else if (storage.getStorage()[0] == null) {
                    status = "Waiting";
                    i--;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }
            if(!paused) {
                status = "Delivering";
                Arrays.fill(transport, null);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public synchronized void pauseThread() {
        paused = true;
    }

    public synchronized void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}