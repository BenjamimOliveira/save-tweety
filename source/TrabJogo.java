import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import processing.sound.*; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TrabJogo extends PApplet {

// alvos aparecem no ecra, x tempo para os eliminar
// alvos eliminanados, nº de alvos aumenta, acrescentam se 5 segundos ao tempo que restou
// repetir até o tempo acabar







Capture video;
int procuraCor;

// Som
AudioIn inputAudio;
Amplitude amp;

// target's
ArrayList<Target> targ = new ArrayList<Target>();
ArrayList<TargetOpc> targOpc = new ArrayList<TargetOpc>();
int nTargets = 3;
int nRedTargs = 2;
int nTwits = 0;

// definiçao dos valores de cada alvo
int targValue = 1;
int redTargValue = 2;
int twitValue = -5;

// inicializaçao do nivel incial como 1
int nivel=1;

int corProximaX = 0;
int corProximaY = 0;

boolean gameOver = false;
boolean jogando = false;

int pontuacao = 0;

PImage cross;
PImage regTarg;
PImage redTarget;
PImage twityTarget;
PImage cup;
PImage ipvc;

Minim minim;
AudioPlayer musicaBack, blast, blastBonus, blastTweety;

boolean corIsSelected=false;

public void setup() {
   
   IniTargets();
   loadImage_();
   loadSons();
   video = new Capture(this, 320, 240, 30);
   video.start();   
}

public void draw() {

   if (!jogando) {
      menuInicial();
      //mic();
      //volume();
   } 
   if (corIsSelected) {
      if (jogando) {
         background(0xff1A237E);
         drawTexto();

         if (gameOver==false) {

            if (targ.size()<=0) {
               nivel++;
               caseNiveis();
               IniTargets();
            }

            targCheckColisao();
            targOpcCheckColisao();

            camRecogni();
         } else {

            fimJogo();
         }
      }
   }else{
      if(jogando){
          selectCorText();
      }
   }
}

public void IniTargets() {
   for (int i = 0; i < nTargets; i++) {
      
      Target target = new Target();
      targ.add(target);
      
   }
   
   for (int i = 0; i < nRedTargs; i++) {
     
      TargetOpc target = new TargetOpc(1);
      targOpc.add(target);
      
   }
   
   for (int i = 0; i < nTwits; i++) {
      
      TargetOpc target = new TargetOpc(2);
      targOpc.add(target);
      
   }
}

public void drawTexto() {

   fill(0xff3F51B5);
   rect(1280, 0, 320, height);

   fill(255);
   textSize(26);
   String nivel_ = "Nivel => " + nivel;
   String dizCor = "Cor selecionada => ";
   String tempo = "Pontuacao => " + pontuacao;

   text(nivel_, 1295, 600);
   text(dizCor, 1295, 300);
   text(tempo, 1295, 450);
}

public void camRecogni() {
   if (video.available()) {
      video.read();
   }

   video.loadPixels();
   scale(-1, 1);
   image(video, -1280, 0, -video.width, video.height);

   float valorInicialCor = 500;

   // -- percorrer todos os pixeis da imagem
   for (int x = 0; x < 320; x++) {
      for (int y = 0; y < 240; y++) {

         int loc = x + y * 320;

         int corAtual = video.pixels[loc];

         float r1 = red(corAtual);
         float g1 = green(corAtual);
         float b1 = blue(corAtual);

         float r2 = red(procuraCor);
         float g2 = green(procuraCor);
         float b2 = blue(procuraCor);

         // -- 
         float distancia = dist(r1, g1, b1, r2, g2, b2);

         if (distancia < valorInicialCor) {
            valorInicialCor = distancia;
            corProximaX = x;
            corProximaY = y;
         }
      }
   }
   scale(-1, 1);
   if (valorInicialCor < 100 && corIsSelected) {
      fill(procuraCor);
      strokeWeight(4);
      stroke(1);
      rect(1550, 283, 32, 18);


      fill(255, 0, 0);
      noStroke();
      ellipse(1600 - corProximaX, corProximaY, 10, 10);

      image(cross, (320 - corProximaX)*(4), (corProximaY)*4);
   }
}

public void mousePressed() {
   int loc;
   
   loc = (320 - mouseX)+ mouseY * 320;
  
   procuraCor = video.pixels[loc];
   corIsSelected = true;
}

// Função responsavel por definir o n de targets por nivel
public void caseNiveis() {

   switch (nivel) {
   case 1: 
      pontuacao = 0;
      break;
   case 2: 
      nTargets = 4;
      targOpc.clear();
      nRedTargs = 2;
      nTwits = 1;
      break;

   case 3: 
      nTargets = 4;
      targOpc.clear();
      nRedTargs = 2;
      nTwits = 2;
      break;

   case 4: 
      nTargets = 5;
      targOpc.clear();
      nRedTargs = 3;
      nTwits = 2;
      break;

   case 5: 
      nTargets = 6;
      targOpc.clear();
      nRedTargs = 3;
      nTwits = 3;
      break;

   case 6: 
      nTargets = 8;
      targOpc.clear();
      nRedTargs = 4;
      nTwits = 4;
      break;

   case 7: 
      nTargets = 4;
      targOpc.clear();
      nRedTargs = 4;
      nTwits = 4;
      break;

   default: 

      gameOver = true;
      break;
   }
}

// não usado
public void mic() {
   amp = new Amplitude(this);
   inputAudio = new AudioIn(this, 0);
   inputAudio.start();      
   amp.input(inputAudio);
}

// não usado
public void volume() {

   float volume = amp.analyze();
   float lim = 0.1f;

   if (volume > lim) {
      jogando = true;  
      inputAudio.stop();
   }
}

public void keyPressed() {

   if (key==ENTER) {
      jogando = true;
      corIsSelected = false;
   }
}

// funcao responsavel pelo ecra de fim de jogo
public void fimJogo() {

   background(0xff5C6BC0);
   fill(0xffC5CAE9);
   textSize(45);
   textAlign(CENTER);
   String fimJogo = "FIM DO JOGO!!!!\n\t Pontuação: " + pontuacao;
   text(fimJogo, width/2, height/2 - 200);
   text("Para reiniciar o jogo pressione \"SPACE\"!!!", width/2, height/2);
   textAlign(CORNER);
   imageMode(CENTER);
   image(cup, width/2, height/2 + 150, 200, 200);
   imageMode(CORNER);
   if (keyPressed) {
      if (key==' ') {

         jogando = false;
         gameOver = false;
         nivel=0;
         targ.clear();
         targOpc.clear();
         pontuacao = 0;
         nTargets = 3;
         nRedTargs = 2;
         nTwits = 0;
      }
   }
}

// verificacao de colisao do targets "normais"
public void targCheckColisao() {

   if (targ.size()>0) {
      for (int j = 0; j < targ.size(); j++) {
         Target tar = targ.get(j);
         tar.moveTarget();

         if (colisaoTarget(tar.x, tar.y, 150, (320 - corProximaX)*(4), (corProximaY)*4, 20)) {

            targ.remove(tar);
            pontuacao = pontuacao + targValue;
            blast.rewind();
            blast.play();

            break;
         }
      }
   }
}

// verificacao de colisao do targets "opcionais"
public void targOpcCheckColisao() {
   if (targOpc.size()>0) {
      for (int j = 0; j < targOpc.size(); j++) {
         TargetOpc tar = targOpc.get(j);
         tar.moveTarget();

         if (colisaoTarget(tar.x, tar.y, 150, (320 - corProximaX)*(4), (corProximaY)*4, 20)) {
            targOpc.remove(tar);
            if (tar.tipo == 1) {
               pontuacao = pontuacao + redTargValue;
               blastBonus.rewind();
               blastBonus.play();
            }
            if (tar.tipo == 2) {
               pontuacao = pontuacao + twitValue;
               blastTweety.rewind();
               blastTweety.play();
            }
            break;
         }
      }
   }
}

// funcao responsavel pelo ecra de inicio de jogo
public void menuInicial() {
   //#5C6BC0 indigo
   background(0xff5C6BC0);
   textSize(50);
   textAlign(CENTER);
   fill(0xffC5CAE9);
   text("Total de 7 rondas!!!", width/2, height/2 + 200);
   text("Pressione \"ENTER\" para começar o jogo!!!", width/2, height/2 + 300);
   textAlign(CORNER);

   imageMode(CENTER);
   rectMode(CENTER);
   textAlign(CENTER);
   noStroke();
   fill(0xff7986CB);
   rect(400, 300, 240, 240);
   image(regTarg, 400, 300, 200, 200);
   fill(0xffC5CAE9);
   text("Obrigatório!", 400, 150);
   text("+1 Ponto!", 400, 500);


   noStroke();
   fill(0xff7986CB);
   rect(800, 300, 240, 240);
   image(redTarget, 800, 300, 200, 200);
   fill(0xffC5CAE9);
   text("Opcional!", 800, 150);
   text("+2 Pontos!", 800, 500);


   noStroke();
   fill(0xff7986CB);
   rect(1200, 300, 240, 240);
   image(twityTarget, 1200, 300, 200, 200);
   fill(0xffC5CAE9);
   text("Opcional!", 1200, 150);
   text("-5 Pontos!", 1200, 500);

   image(ipvc, 70, height -65, 123, 93);
   
   textAlign(RIGHT);
   textSize(20);
   text("Jogo desenvolvido no âmbito da Unidade Curricular: \"Tecnologias Multimédia\"\nEngenharia Informática 2017/1018 ESTG-IPVC\nBenjamim Oliveira Nº 19160", width - 20, height -80); 
   textAlign(CORNER);
   imageMode(CORNER);
   rectMode(CORNER);
   
}

// funcao responsavel por carregar todos os sons
public void loadSons() {
   minim = new Minim(this);
   musicaBack = minim.loadFile("sons\\Boris_Brejcha_Take_My_Space.mp3");
   musicaBack.loop();

   minim = new Minim(this);
   blast = minim.loadFile("sons\\Blast.wav");

   minim = new Minim(this);
   blastBonus = minim.loadFile("sons\\BlastBonus.wav");

   minim = new Minim(this);
   blastTweety = minim.loadFile("sons\\BlastTweety.wav");
}

//
public void selectCorText(){
          background(0xff1A237E);
          drawTexto();
          camRecogni();  
          textSize(50);
          fill(0xff3F51B5);
          rect(width/2 - 450, height/2-50, 625, 70);
          fill(0xffC5CAE9);
          text("Selecione a cor a seguir",width/2 - 425,height/2);
         
}

// funcao responsavel por carregar todas imagens
public void loadImage_(){
   cross = loadImage("cross.png");
   cross.resize(100, 100);
   regTarg = loadImage("target.png");
   redTarget = loadImage("redTarget.png");
   twityTarget = loadImage("twit.png");
   cup = loadImage("cup.png");
   ipvc = loadImage("ipvc.png");
}
public boolean colisaoTarget(float x1, float y1, float d1, float x2, float y2, float d2){
   
   //teorema de pitágoras
   float xDist = x1 - x2;
   float yDist = y1 - y2;
   //distancia == hipotenusa
   float distancia = sqrt((xDist * xDist) + (yDist * yDist));
   
   if(d1/2 + d2/2 > distancia){
     return true;
   }else{
     return false;
   }
}
public class Target {
   
   //pos inicial
   float x, y;
   
   //velocidade no eixo X
   float velocidadeX;
   
   //velocidade no eixo Y
   float velocidadeY;
   
   //velocidade geral;
   float velocidade;
   
   //direçao do alvo
   float direcao;
   
   //imagem => target
   PImage target = loadImage("target.png");; 
   
   
   Target(){
      
      //randomização da posicçao inicial do alvo
      x = random(0, 1) * (width - 320 - 75);
      y = random(0, 1) * (height - 150);
      
      //randomizaçao da velocidade inicial do alvo
      velocidade = random(10);
      
      // calcula a direçao assumida pelo alvo, entre 0 e 2pi
      direcao = random(0,1) * 2 * PI;
      
      //velocidade conforme a direçao
      //ex: se direçao for == a 2Pi 
      //velocidadeX = velocidade * 1;
      //velocidadeY = velocidade * 0;
      //representando assim
      velocidadeX = velocidade * cos(direcao);
      velocidadeY = velocidade * sin(direcao);
   }
   
   public void moveTarget(){
      //movimento no x calculado através do x anterior + a velocidade no eixoX
      x = x + velocidadeX;
      //movimento no y calculado através do y anterior + a velocidade no eixoY
      y = y + velocidadeY;
      
      // redimensionamento do tamanho do alvo para 150 pixeis por 150 pixeis
      target.resize(150, 150);
      
      //desenha o alvo na posiçao para a qual ele acabou de se "mover"
      imageMode(CENTER);
      image(target, x, y);
      imageMode(CORNER);
      
      //limite direito da janela de jogo
      //se x ultrapassar 1280 - largura do alvo(largura máxima da area jogavel) 
      //x é posicionado na posiçao 1280 - largura do alvo (para evitar que este fique preso)
      //a velocidade do alvo segundo o eixo do x assume o valor simétrico ao valor que tinha antes de atingir o limite direito
      if(x > 1280 - target.width/2){
          x = 1280 - target.width/2;
          velocidadeX = -velocidadeX; 
      }
      
      //limite esquerdo da janela de jogo
      if(x < 0 + target.width/2){
         x = target.width/2;
          velocidadeX = -velocidadeX; 
       }
       
       //limite inferior da janela de jogo
       if(y > height - target.height/2){
         y= height - target.height/2;
          velocidadeY = -velocidadeY; 
       }
       
       //limite superior da janela dejogo
       if(y - 50< 0 + target.height/2){
         y = target.height/2 + 50;
          velocidadeY = -velocidadeY; 
       }
   }
   
   
   
}
//targets opcionais
public class TargetOpc{
   
   //pos inicial
   float x, y;
   
   // 1=> red 2=> tweety
   int tipo;
   
   //velocidade
   float velocidadeX, velocidadeY, velocidade, direcao;
   
   //imagem => target
   PImage target; 
   
   //1=> redTarget 2=> twity
   TargetOpc(int image){
      
      if(image == 1){ target = loadImage("redTarget.png"); tipo = 1;}
      if(image == 2){ target = loadImage("twit.png"); tipo = 2;}
      
      //randomização da posicçao inicial do alvo
      x = random(0, 1) * (width - 75);
      y = random(0, 1) * (width - 75);
      
      //randomizaçao da velocidade inicial do alvo
      velocidade = random(10);
      
      //calcula a direçao assumida pelo alvo, entre 0 e 2pi
      direcao = random(0,1) * 2 * PI;
      
      //velocidade conforme a direçao
      //ex: se direçao for == a 2Pi 
      //velocidadeX = velocidade * 1;
      //velocidadeY = velocidade * 0;
      //representando assim a direçao fornecida pelo random
      velocidadeX = velocidade * cos(direcao);
      velocidadeY = velocidade * sin(direcao);
   }
   
   public void moveTarget(){
      //movimento no x calculado através do x anterior + a velocidade no eixoX
      x = x + velocidadeX;
      
      //movimento no y calculado através do y anterior + a velocidade no eixoY
      y = y + velocidadeY;
      
      // redimensionamento do tamanho do alvo para 150 pixeis por 150 pixeis
      target.resize(150, 150);
      
      //desenha o alvo na posiçao para a qual ele acabou de se "mover"
      imageMode(CENTER);
      image(target, x, y);
      imageMode(CORNER);
      
      //limite direito da janela de jogo
      //se x ultrapassar 1280 - largura do alvo(largura máxima da area jogavel) 
      //x é posicionado na posiçao 1280 - largura do alvo (para evitar que este fique preso)
      //a velocidade do alvo segundo o eixo do x assume o valor simétrico ao valor que tinha antes de atingir o limite direito
      if(x > 1280 - target.width/2){
          x = 1280 - target.width/2;
          velocidadeX = -velocidadeX; 
      }
      //limite esquerdo da janela de jogo
      if(x < 0 + target.width/2){
         x = target.width/2;
          velocidadeX = -velocidadeX; 
       }
       //limite inferior da janela de jogo
       if(y > height - target.height/2){
         y= height - target.height/2;
          velocidadeY = -velocidadeY; 
       }
       //limite superior da janela de jogo
       if(y - 50< 0 + target.height/2){
         y = target.height/2 + 50;
          velocidadeY = -velocidadeY; 
       }
   }
   
   
   
}
   public void settings() {  size (1600, 960); }
   static public void main(String[] passedArgs) {
      String[] appletArgs = new String[] { "--present", "--window-color=#050000", "--hide-stop", "TrabJogo" };
      if (passedArgs != null) {
        PApplet.main(concat(appletArgs, passedArgs));
      } else {
        PApplet.main(appletArgs);
      }
   }
}
