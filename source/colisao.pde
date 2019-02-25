boolean colisaoTarget(float x1, float y1, float d1, float x2, float y2, float d2){
   
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
