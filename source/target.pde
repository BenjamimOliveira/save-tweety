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
   
   void moveTarget(){
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
