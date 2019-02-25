// alvos aparecem no ecra, x tempo para os eliminar
// alvos eliminanados, nº de alvos aumenta, acrescentam se 5 segundos ao tempo que restou
// repetir até o tempo acabar

import processing.video.*;
import processing.sound.*;
import ddf.minim.*;



Capture video;
color procuraCor;

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

void setup() {
   size (1600, 960);
   IniTargets();
   loadImage_();
   loadSons();
   video = new Capture(this, 320, 240, 30);
   video.start();   
}

void draw() {

   if (!jogando) {
      menuInicial();
      //mic();
      //volume();
   } 
   if (corIsSelected) {
      if (jogando) {
         background(#1A237E);
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

void IniTargets() {
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

void drawTexto() {

   fill(#3F51B5);
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

void camRecogni() {
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

         color corAtual = video.pixels[loc];

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

void mousePressed() {
   int loc;
   
   loc = (320 - mouseX)+ mouseY * 320;
  
   procuraCor = video.pixels[loc];
   corIsSelected = true;
}

// Função responsavel por definir o n de targets por nivel
void caseNiveis() {

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
void mic() {
   amp = new Amplitude(this);
   inputAudio = new AudioIn(this, 0);
   inputAudio.start();      
   amp.input(inputAudio);
}

// não usado
void volume() {

   float volume = amp.analyze();
   float lim = 0.1;

   if (volume > lim) {
      jogando = true;  
      inputAudio.stop();
   }
}

void keyPressed() {

   if (key==ENTER) {
      jogando = true;
      corIsSelected = false;
   }
}

// funcao responsavel pelo ecra de fim de jogo
void fimJogo() {

   background(#5C6BC0);
   fill(#C5CAE9);
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
void targCheckColisao() {

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
void targOpcCheckColisao() {
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
void menuInicial() {
   //#5C6BC0 indigo
   background(#5C6BC0);
   textSize(50);
   textAlign(CENTER);
   fill(#C5CAE9);
   text("Total de 7 rondas!!!", width/2, height/2 + 200);
   text("Pressione \"ENTER\" para começar o jogo!!!", width/2, height/2 + 300);
   textAlign(CORNER);

   imageMode(CENTER);
   rectMode(CENTER);
   textAlign(CENTER);
   noStroke();
   fill(#7986CB);
   rect(400, 300, 240, 240);
   image(regTarg, 400, 300, 200, 200);
   fill(#C5CAE9);
   text("Obrigatório!", 400, 150);
   text("+1 Ponto!", 400, 500);


   noStroke();
   fill(#7986CB);
   rect(800, 300, 240, 240);
   image(redTarget, 800, 300, 200, 200);
   fill(#C5CAE9);
   text("Opcional!", 800, 150);
   text("+2 Pontos!", 800, 500);


   noStroke();
   fill(#7986CB);
   rect(1200, 300, 240, 240);
   image(twityTarget, 1200, 300, 200, 200);
   fill(#C5CAE9);
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
void loadSons() {
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
void selectCorText(){
          background(#1A237E);
          drawTexto();
          camRecogni();  
          textSize(50);
          fill(#3F51B5);
          rect(width/2 - 450, height/2-50, 625, 70);
          fill(#C5CAE9);
          text("Selecione a cor a seguir",width/2 - 425,height/2);
         
}

// funcao responsavel por carregar todas imagens
void loadImage_(){
   cross = loadImage("cross.png");
   cross.resize(100, 100);
   regTarg = loadImage("target.png");
   redTarget = loadImage("redTarget.png");
   twityTarget = loadImage("twit.png");
   cup = loadImage("cup.png");
   ipvc = loadImage("ipvc.png");
}
