package br.dev.marcosvirgilio.mobile.ctrlentregas;

import androidx.navigation.NavController;

import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;

public class Singleton {
    private static final Singleton instance = new Singleton();
    private Aluno aluno = new Aluno();
    private NavController navController = null;
    private Singleton() {}

    public static Singleton getInstance() {
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
}
