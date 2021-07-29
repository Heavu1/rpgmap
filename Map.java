
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class Map extends JFrame implements MapInterface {

    static int EleWidth = 50;
    static int EleLength = 50;
    //地图大小
    static int MapWidth = 800;
    static int MapLength= 800;
    //地图保存的位置
    static String path = "map1.map";
    //将所有的图片素材对象放入一个数组中，便于窗体上的下拉列表添加所有的图片素材
    ArrayList<Object> allicons = new ArrayList<>();

    //图片下拉表
    JComboBox<ImageIcon> picbox;
    //层数下拉表
    JComboBox<Integer> layerbox;
    //建立两层map，不同 map 间无法通行。
    // 用数组表示，用来记录地图块填充序号
    int[][] map1 = new int[MapLength / EleLength][MapLength / EleLength];
    int[][] map2 = new int[MapLength / EleLength][MapLength / EleLength];
    // 预设 ImageIcon 占据画布
    ImageIcon[][] icons1 = new ImageIcon[MapLength / EleLength][MapLength / EleLength];
    ImageIcon[][] icons2 = new ImageIcon[MapLength / EleLength][MapLength / EleLength];

    //编辑中的地图显示的面板
    MyPanel panel;

    /**
     * 程序入口主函数
     *
     * @param args
     */
    public static void main(String[] args) {
        Map map = new Map();
        map.init();
    }


    /**
     * 将目录文件插入 allicons 集合中
     */
    public void PicInsert() {
        String path = "D:\\IntelliJ IDEA\\maprpg";
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            ImageIcon I = new ImageIcon(files[i].getName());
            allicons.add(I);
        }
    }

    /**
     * 设置窗体
     */
    public void init() {
        this.setTitle("RPG地图生成器");
        this.setSize(800, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//JFrame.EXIT_ON_CLOSE
        this.setLayout(new FlowLayout());
        //设置面板类
        panel = new MyPanel();
        panel.setPreferredSize(new Dimension(MapWidth, MapLength));
        JScrollPane jsp = new JScrollPane(panel);
        jsp.setPreferredSize(new Dimension(600, 400));
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //创建选择素材层的下拉列表 (这里选择1表示当前编辑的是第一层的元素，2表示的当前编辑第二层元素，3同前面)
        layerbox = new JComboBox<Integer>();
        layerbox.addItem(1);
        layerbox.addItem(2);

        //创建下拉列表选择地图快，将地图快加入下拉表
        picbox = new JComboBox<ImageIcon>();
        AddEle(picbox);

        //创建按钮
        JButton create = new JButton("创建");
        JButton unbtn = new JButton("撤销");
        JButton rebtn = new JButton("恢复");
        create.setActionCommand("create");
        unbtn.setActionCommand("undobtn");
        rebtn.setActionCommand("redobtn");

        //添加窗体所有结构        
        this.add(jsp);
        this.add(layerbox);
        this.add(picbox);
        this.add(create);
        this.add(unbtn);
        this.add(rebtn);


        //给面板安装鼠标监听器
        PanelListenner plis = new PanelListenner();
        panel.addMouseListener(plis);


        //给按钮安装事件监听器
        Buttonlistenner blis = new Buttonlistenner();
        create.addActionListener(blis);
        rebtn.addActionListener(blis);
        unbtn.addActionListener(blis);


        this.setVisible(true);


    }


    //向下拉列表添加地图块
    public void AddEle(JComboBox picbox) {
        PicInsert();
        for (Object allicon : allicons) {
            picbox.addItem((ImageIcon) allicon);
        }
    }


    class MyPanel extends JPanel {

        //将数组下标转化成对应的图片左上角坐标
        public int getX(int j) {
            int x = j * EleLength;
            return x;
        }

        //将数组下标转化成对应的图片左上角坐标
        public int getY(int i) {
            int y = i * EleWidth;
            return y;
        }


        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int i = 0; i < MapLength / EleLength; i++) {
                for (int j = 0; j < MapWidth / EleWidth; j++) {
                    //铺设第一层地图块
                    if (icons1[i][j] != null) {
                        g.drawImage(icons1[i][j].getImage(), getX(j), getY(i), EleWidth, EleLength, null);
                    }
                    //铺设第二层地图块
                    if (icons2[i][j] != null) {
                        g.drawImage(icons2[i][j].getImage(), getX(j), getY(i), EleWidth, EleLength, null);
                    }
                }
            }
        }
    }


    class Buttonlistenner implements ActionListener, undostk, redostk {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("create")) {
                try {
                    System.out.println("saving begin");
                    FileOutputStream fos = new FileOutputStream(path);
                    DataOutputStream dos = new DataOutputStream(fos);
                    int i = MapLength / EleLength;
                    int j = MapWidth / EleWidth;
                    dos.writeInt(i);
                    dos.writeInt(j);
                    for (int ii = 0; ii < i; ii++) {
                        for (int jj = 0; jj < j; jj++) {
                            dos.writeInt(map1[ii][jj]);
                            dos.writeInt(map2[ii][jj]);
                        }
                    }
                    dos.flush();
                    dos.close();
                    System.out.println("save successfully");

                } catch (Exception ef) {
                    ef.printStackTrace();
                }
            }
            if (e.getActionCommand().equals("undobtn") && (int) layerbox.getSelectedItem() == 1) {
                try {
                    ImageIcon icon = (ImageIcon) undo_iconstk1.pop();
                    int[] ij = (int[]) undo_posstk1.pop();
                    redo_iconstk1.push(icon);
                    redo_posstk1.push(ij);
                    icons1[ij[0]][ij[1]] = null;
                }catch (EmptyStackException ESE){
                    ESE.printStackTrace();
                    System.out.println("UndoStack1 empty!");
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.out.println("RedoStack1 full!");
                }
                panel.repaint();
            }
            if (e.getActionCommand().equals("undobtn") && (int) layerbox.getSelectedItem() == 2) {
                try {
                    ImageIcon icon = (ImageIcon) undo_iconstk2.pop();
                    int[] ij = (int[]) undo_posstk2.pop();
                    redo_iconstk2.push(icon);
                    redo_posstk2.push(ij);
                    icons2[ij[0]][ij[1]] = null;
                }catch (EmptyStackException ESE){
                    ESE.printStackTrace();
                    System.out.println("UndoStack2 empty!");
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println("RedoStack2 full!");
                }
                panel.repaint();
            }
            if (e.getActionCommand().equals("redobtn") && (int) layerbox.getSelectedItem() == 1) {
                try {
                    ImageIcon icon = (ImageIcon) redo_iconstk1.pop();
                    int[] ij = (int[]) redo_posstk1.pop();
                    undo_iconstk1.push(icon);
                    undo_posstk1.push(icon);
                    icons1[ij[0]][ij[1]] = icon;
                }catch (EmptyStackException ESE){
                    ESE.printStackTrace();
                    System.out.println("RedoStack1 empty!");
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println("UndoStack1 full!");
                }
                panel.repaint();
            }
            if (e.getActionCommand().equals("redobtn") && (int) layerbox.getSelectedItem() == 2) {
                try {
                    ImageIcon icon = (ImageIcon) redo_iconstk2.pop();
                    int[] ij = (int[]) redo_posstk2.pop();
                    undo_iconstk2.push(icon);
                    undo_posstk2.push(icon);
                    icons2[ij[0]][ij[1]] = icon;
                }catch (EmptyStackException ESE){
                    ESE.printStackTrace();
                    System.out.println("RedoStack2 empty!");
                }catch (Exception ex){
                    ex.printStackTrace();
                    System.out.println("UndoStack2 full!");
                }
                panel.repaint();
            }
        }
    }




    class PanelListenner extends MouseAdapter implements undostk, redostk {
        /**
         * 将一个三位的字符串，转为一个int
         * The first char value is at index 0
         */


        public int numstr2int(String numstr) {
            for (int i = 0; i < 3; i++) {
                if (numstr.charAt(i) != 0) {
                    numstr = numstr.substring(i);
                    int num = Integer.parseInt(numstr);
                    return num;
                }
            }
            numstr = numstr.substring(2);
            int num = Integer.parseInt(numstr);
            return num;
        }

        public void mouseClicked(MouseEvent e) {
            //得到点击位置坐标
            int i = e.getY() / EleLength;
            int j = e.getX() / EleWidth;

            //使用 .png 文件名前三位数字作为地图块序号
            ImageIcon icon = (ImageIcon) picbox.getSelectedItem();
            int num = numstr2int(icon.toString().substring(0, 3));

            //添加地图快，方法是修改数组的值
            if ((int) layerbox.getSelectedItem() == 1) {
                map1[i][j] = num;
                icons1[i][j] = icon;
                undo_iconstk1.push(icon);
                undo_posstk1.push(new int[]{i,j});
            } else if ((int) layerbox.getSelectedItem() == 2) {
                map2[i][j] = num;
                icons2[i][j] = icon;
                undo_iconstk2.push(icon);
                undo_posstk2.push(new int[]{i,j});

            }


            panel.repaint();
        }

    }

}
