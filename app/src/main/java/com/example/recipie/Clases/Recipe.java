package com.example.recipie.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    public String id;
    public String name;
    public String desc;
    public String owner;
    public String foto;
    public Boolean privacy;
    public ArrayList<String> ingredients;
    public ArrayList<String> directions;
    public ArrayList<String> comments;
}
