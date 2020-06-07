package com.company;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

public class Logic extends JFrame
{
    enum RodzajSzyfrowania
    {
        CEZAR("Caesar","ces"),
        AES("AES", "aes", "moj klucz do AES"),
        DES("DES","des", "moj klucz do DES"),
        UNKNOWN("UNKNOWN","");

        private String name;
        private String key;
        private String extension;

        RodzajSzyfrowania(String pName, String pExt)
        {
            name = pName;
            extension = pExt;
        }

        RodzajSzyfrowania(String pName, String pExt, String pKey)
        {
            name = pName;
            extension = pExt;
            key = pKey;
        }

        String getName()
        {
            return name;
        }

        String getKey()
        {
            return key;
        }
        String getExt()
        {
            return extension;
        }
    }

    private RodzajSzyfrowania rodzajSzyfr = RodzajSzyfrowania.CEZAR;

    String podajNazwePliku = "Podaj nazwe pliku to szyfrowania za pomocą ";
    private JLabel labelDes = new JLabel(podajNazwePliku + RodzajSzyfrowania.DES.getName());
    private JLabel labelCezar = new JLabel(podajNazwePliku + RodzajSzyfrowania.CEZAR.getName());
    private JLabel labelAes = new JLabel(podajNazwePliku + RodzajSzyfrowania.AES.getName());

    private JLabel labelNieznaneSzyfrowanie = new JLabel("Nie mozna rozszyfrowac plik o podanym rozszerzeniu !");
    private JLabel labelPlikZaszyfrowano = new JLabel("Plik zostal zaszyfrowany.");
    private JLabel labelPlikOdszyfrowano = new JLabel("Plik zostal odszyfrowany.");


    public Logic(String title) throws HeadlessException
    {
        super(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(null);

        przygotujEkran();

        dodajMenuSzyfruj();
    }


    private void przygotujEkran()
    {
        labelCezar.setBounds(10, 10, 400, 30);
        add(labelCezar);
        JTextField plik = new JTextField();
        plik.setBounds(10, 50, 400, 30);
        add(plik);
        JButton deszyfruj = new JButton("Deszyfruj");
        deszyfruj.setBounds(130, 120, 100, 30);
        add(deszyfruj);
        JButton szyfruj = new JButton("Szyfruj");
        szyfruj.setBounds(40, 120, 100, 30);
        add(szyfruj);
        szyfruj.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                remove(labelPlikOdszyfrowano);
                remove(labelNieznaneSzyfrowanie);
                repaint(); // odwiezanie okna
                try {
                    File inputFile = new File(plik.getText());
                    File outputFile = new File( inputFile.getPath() + "." + getRodzajSzyfr().getExt());
                    szyfruj(inputFile, outputFile);
                    labelPlikZaszyfrowano.setBounds(10, 80, 400, 30);
                    add(labelPlikZaszyfrowano);
                    repaint();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.out.println("Zła nazwa");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NoSuchPaddingException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (InvalidKeyException ex) {
                    ex.printStackTrace();
                } catch (IllegalBlockSizeException ex) {
                    ex.printStackTrace();
                } catch (BadPaddingException ex) {
                    ex.printStackTrace();
                }
            }
        });
        deszyfruj.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                remove(labelPlikZaszyfrowano);
                remove(labelNieznaneSzyfrowanie);
                repaint();
                try {
                    RodzajSzyfrowania rodzajSzyfrowaniaPliku = jakieSzyfrowanie(plik.getText()); //
                    if (!rodzajSzyfrowaniaPliku.getName().equals(RodzajSzyfrowania.UNKNOWN.getName())) {
                        File plikZaszyfrowany = new File(plik.getText());
                        File plikOdszyfrowany = new File(dajNazwePlikuOdszyfrowanego(plik.getText()));
                        deszyfruj(rodzajSzyfrowaniaPliku, plikZaszyfrowany, plikOdszyfrowany);
                        labelPlikOdszyfrowano.setBounds(10, 80, 400, 30);
                        add(labelPlikOdszyfrowano);
                        repaint();
                    } else {
                        System.out.println("Nie mozna rozszyfrowac plik o podanym rozszerzeniu !");
                        labelNieznaneSzyfrowanie.setBounds(10, 80, 400, 30);
                        add(labelNieznaneSzyfrowanie);
                        repaint();
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.out.println("Zła nazwa");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NoSuchPaddingException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (InvalidKeyException ex) {
                    ex.printStackTrace();
                } catch (IllegalBlockSizeException ex) {
                    ex.printStackTrace();
                } catch (BadPaddingException ex) {
                    ex.printStackTrace();
                }
            }
        });
        repaint();
    }


    private String dajNazwePlikuOdszyfrowanego(String nazwaZaszyfrowanegoPliku)
    {
        String nazwaPliku ="Odszyfrowany " + rodzajSzyfr.extension +" "+ nazwaZaszyfrowanegoPliku.substring(0,nazwaZaszyfrowanegoPliku.length() - 4);
        return nazwaPliku;
    }


    private RodzajSzyfrowania jakieSzyfrowanie(String nazwaZaszyfrowanegoPliku)
    {
        RodzajSzyfrowania jakiRodzajSzydfrowania = RodzajSzyfrowania.UNKNOWN;
        String rozszerzeniePliku = nazwaZaszyfrowanegoPliku.substring(nazwaZaszyfrowanegoPliku.length() - 3);
        if (rozszerzeniePliku.equalsIgnoreCase(RodzajSzyfrowania.CEZAR.getExt())) {
            jakiRodzajSzydfrowania = RodzajSzyfrowania.CEZAR;
        } else if (rozszerzeniePliku.equalsIgnoreCase(RodzajSzyfrowania.AES.getExt())) {
            jakiRodzajSzydfrowania = RodzajSzyfrowania.AES;
        } else if (rozszerzeniePliku.equalsIgnoreCase(RodzajSzyfrowania.DES.getExt())) {
            jakiRodzajSzydfrowania = RodzajSzyfrowania.DES;
        }
        return jakiRodzajSzydfrowania;
    }

    public RodzajSzyfrowania getRodzajSzyfr()
    {
        return rodzajSzyfr;
    }

    public void setRodzajSzyfr(RodzajSzyfrowania rodzajSzyfr)
    {
        this.rodzajSzyfr = rodzajSzyfr;
    }


    private void dodajMenuSzyfruj()
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        JMenuBar menuBar = new JMenuBar();
        JMenu typSzyfrowania = new JMenu("Szyfr");
        JMenuItem cezar = new JMenuItem(new AbstractAction(RodzajSzyfrowania.CEZAR.getName())
        {
            public void actionPerformed(ActionEvent e)
            {
                remove(labelAes);
                remove(labelDes);
                remove(labelNieznaneSzyfrowanie);
                remove(labelPlikZaszyfrowano);
                remove(labelPlikOdszyfrowano);
                labelCezar.setBounds(10, 10, 400, 30);
                add(labelCezar);
                repaint();
                setRodzajSzyfr(RodzajSzyfrowania.CEZAR);
            }
        });
        typSzyfrowania.add(cezar);

        JMenuItem aes = new JMenuItem(new AbstractAction(RodzajSzyfrowania.AES.getName())
        {
            public void actionPerformed(ActionEvent e)
            {
                remove(labelCezar);
                remove(labelDes);
                remove(labelNieznaneSzyfrowanie);
                remove(labelPlikZaszyfrowano);
                remove(labelPlikOdszyfrowano);
                labelAes.setBounds(10, 10, 400, 30);
                add(labelAes);
                repaint();
                setRodzajSzyfr(RodzajSzyfrowania.AES);
            }
        });
        typSzyfrowania.add(aes);

        JMenuItem des = new JMenuItem(new AbstractAction(RodzajSzyfrowania.DES.getName())
        {
            public void actionPerformed(ActionEvent e)
            {
                remove(labelCezar);
                remove(labelAes);
                remove(labelNieznaneSzyfrowanie);
                remove(labelPlikZaszyfrowano);
                remove(labelPlikOdszyfrowano);
                labelDes.setBounds(10, 10, 400, 30);
                add(labelDes);
                repaint();
                setRodzajSzyfr(RodzajSzyfrowania.DES);
            }
        });
        typSzyfrowania.add(des);

        menuBar.add(typSzyfrowania);
        setJMenuBar(menuBar);
        setVisible(true);
    }


    public ArrayList<String> wczytaj(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);
        ArrayList<String> tekst = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String linijka = scanner.nextLine();
            tekst.add(linijka);

        }
        return tekst;
    }

    public void szyfruj(File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        switch (getRodzajSzyfr()) {
            case CEZAR:
                szyfrujCezarem(inputFile, outputFile);
                break;
            case AES:
                szyfrujAES(inputFile, outputFile);
                break;
            case DES:
                szyfrujDES(inputFile, outputFile);
                break;
        }
    }

    private void szyfrujAES(File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        doCrypto(RodzajSzyfrowania.AES, Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }

    private void szyfrujDES(File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        doCrypto(RodzajSzyfrowania.DES, Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }

    public void szyfrujCezarem(File inputFile, File outputFile)
    {
        String text = null;
        try {
            text = wczytaj(inputFile).toString();
            text=text.substring(1,text.length()-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char[] tablica = text.toCharArray();
        String zakodowanyTekst = "";
        for (char ele : tablica) {
            if (ele >= 'A' && ele <= 'Z') {
                ele += 3;
                ele = (char) ((ele - 'A') % 26 + 'A');
            } else if (ele >= 'a' && ele <= 'z') {
                ele += 3;
                ele = (char) ((ele - 'a') % 26 + 'a');
            }
            zakodowanyTekst += ele;
        }
        try {
            zapisz(zakodowanyTekst, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //
    public void deszyfruj(RodzajSzyfrowania rodzajSzyfr, File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        switch (rodzajSzyfr) {
            case CEZAR:
                deszyfrujCezarem(inputFile, outputFile);
                break;
            case AES:
                deszyfrujAES(inputFile, outputFile);
                break;
            case DES:
                deszyfrujDES( inputFile, outputFile);
                break;
        }
    }


    public void deszyfrujCezarem(File inputFile, File outputFile)
    {
        String zdekodowanyTeks="";
        String text="";
        try {
            text = wczytaj(inputFile).toString();
            text=text.substring(1,text.length()-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        char[] litera = text.toCharArray();

        for (char ele : litera) {
            if (ele > 'C' && ele <= 'Z' || ele > 'c' && ele <= 'z') {
                ele -= 3;
            }
            else if (ele=='A'){
                ele='X' ;
            }
            else if (ele=='B'){
                ele= 'Y';
            }
            else if (ele=='C'){
                ele='Z';
            }
            else if (ele=='a'){
                ele='x';
            }
            else if (ele=='b'){
                ele='y';
            }
            else if (ele=='c'){
                ele='z';
            }
            zdekodowanyTeks += ele;
        }
        try {
            zapisz(zdekodowanyTeks, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deszyfrujAES(File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        doCrypto(RodzajSzyfrowania.AES, Cipher.DECRYPT_MODE, inputFile, outputFile);
    }

    public void deszyfrujDES(File inputFile, File outputFile) throws BadPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        doCrypto(RodzajSzyfrowania.DES, Cipher.DECRYPT_MODE, inputFile, outputFile);
    }


    public void zapisz(String text, File file) throws IOException
    {
        File fileNew = new File(file.getPath());
        fileNew.createNewFile();
        PrintWriter writer = new PrintWriter(fileNew.getPath(), "UTF8");
        writer.println(text);
        writer.close();

    }

    private void doCrypto(RodzajSzyfrowania rodzajSzyfr, int cipherMode, File inputFile, File outputFile) throws IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
    {
        Cipher cipher = Cipher.getInstance(rodzajSzyfr.getName());

        switch (rodzajSzyfr) {
            case AES:
                Key secretKey = new SecretKeySpec(rodzajSzyfr.getKey().getBytes(), rodzajSzyfr.getName());
                cipher.init(cipherMode, secretKey);
                break;
            case DES:
                try {
                    Key myDesKey = SecretKeyFactory.getInstance(rodzajSzyfr.getName()).generateSecret( new DESKeySpec(rodzajSzyfr.getKey().getBytes()));
                    cipher.init(cipherMode, myDesKey);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                break;
        }
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }
}