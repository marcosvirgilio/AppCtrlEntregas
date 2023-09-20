package br.dev.marcosvirgilio.mobile.ctrlentregas.util;

import android.content.Context;

import androidx.navigation.NavController;

import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;

public class SingletonNavigation {
    private static SingletonNavigation instance;
    private Aluno aluno = new Aluno();
    private NavController navController = null;
    private String mensagemErro = "";

    private SingletonNavigation() {}

    public static synchronized SingletonNavigation getInstance() {
        if (instance == null) {
            instance = new SingletonNavigation();
        }
        return instance;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }
    public NavController getNavController() {
        return navController;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    
    public void setMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }


}
