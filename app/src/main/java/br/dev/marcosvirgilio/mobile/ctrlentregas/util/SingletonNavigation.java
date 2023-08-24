package br.dev.marcosvirgilio.mobile.ctrlentregas.util;

import androidx.navigation.NavController;

import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;

public class SingletonNavigation {
    private static final SingletonNavigation instance = new SingletonNavigation();
    private Aluno aluno = new Aluno();
    private NavController navController = null;
    private String mensagemErro = "";

    private SingletonNavigation() {}

    public static SingletonNavigation getInstance() {
        return instance;
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

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }


}
