package com.estudo.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	 //Iniciação
    private SpriteBatch batch;
	//Sprites
    private  Texture[] passaro;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoCima;
    private BitmapFont fontePontuacao;
    private Texture gameOver;
    private Timer wait;
    //Tamanho da tela (x,y)
    float eixoX;
    float eixoY;
    //

    private int gameState = 0;
    private int pontuacao = 0;
    private boolean marcou ;
	//Animação
	private float animacaoPassaro = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialY;

	private float posicaoHorizontalCano;
	private float espacoCanos;
	private float deltaTime;
	private Random numeroRandom;
	private float alturaCanosRandom;

	//Shapes Colisões //

    private Circle circuloPassaro;
    private Rectangle rectangleCanoCima;
    private Rectangle rectangleCanoBaixo;
    // private ShapeRenderer shape;


    //Camera e ViewPort
    private OrthographicCamera camera;
    private Viewport viewport;

    private final float VIRTUAL_X = 768;
    private final float VIRTUAL_Y = 1024;


	@Override
	public void create () {
        eixoX = VIRTUAL_X; //Gdx.graphics.getWidth();
        eixoY = VIRTUAL_Y; //Gdx.graphics.getHeight();


        posicaoInicialY =  12; //eixoY / 2;
        posicaoHorizontalCano = eixoX;

        espacoCanos = 400;
        batch = new SpriteBatch();

        //Colision
        circuloPassaro = new Circle();
        rectangleCanoBaixo = new Rectangle();
        rectangleCanoCima = new Rectangle();
        //shape = new ShapeRenderer();


            //Texture[passaro] (Array)
        passaro = new Texture[3];
        passaro[0] = new Texture("passaro5.png");
        passaro[1] = new Texture("passaro6.png");
        passaro[2] = new Texture("passaro4.png");
            //

            //Texture (Canos)
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoCima = new Texture("cano_topo_maior.png");

            //Texture GameOver
        gameOver = new Texture("game_over.png");


            //Instanciando o Fundo (Tipo "Texture")
        fundo = new Texture("fundo.png");
            //
        fontePontuacao = new BitmapFont();
        fontePontuacao.setColor(Color.WHITE);
        fontePontuacao.getData().setScale(4);

        wait = new Timer();


        /* Camera Config */
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_X / 2, VIRTUAL_Y / 2, 0);
        viewport = new StretchViewport(VIRTUAL_X,VIRTUAL_Y, camera);




	}
	 public void render () {

	    camera.update();

	    //limpar frames <
         Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
	    //Animando bater de asas suavizado
        animacaoPassaro += deltaTime * 7;

        //Loop Bater de asas
        if (animacaoPassaro > 2) animacaoPassaro = 0;
        // ---




        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        }else {

            velocidadeQueda++;
            if (posicaoInicialY > 0)
                posicaoInicialY = posicaoInicialY - velocidadeQueda;


            if (gameState == 1) {
                posicaoHorizontalCano -= deltaTime * 500;
                if (Gdx.input.justTouched())
                    velocidadeQueda = -20;
                if (posicaoHorizontalCano < -canoCima.getWidth()) {
                    posicaoHorizontalCano = eixoX + 75;
                    marcou = false;

                    alturaCanosRandom = numeroRandom.nextInt(700) - 350;
                    Gdx.app.log("LOG", "LOG:" + alturaCanosRandom);
                }
                if (posicaoHorizontalCano < 120) {
                    if (!marcou)
                        pontuacao++;
                    marcou = true;

                    numeroRandom = new Random();
                }
            } else if (gameState == 2) { //GameOVer
                if (Gdx.input.justTouched()) {
                    wait.delay(1000);
                    gameState = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialY = eixoY / 2;
                    posicaoHorizontalCano = eixoX;

                }
            }

        }

            //projeção da camera
            batch.setProjectionMatrix(camera.combined);



            //Começo do Render
            batch.begin();
            //Desenha Fundo
            batch.draw(fundo, 0, 0, eixoX, eixoY);
            //Desenha Cano
            batch.draw(canoCima, posicaoHorizontalCano - 100, eixoY / 2 + espacoCanos / 2 + alturaCanosRandom);
            batch.draw(canoBaixo, posicaoHorizontalCano - 100, eixoY / 2 - canoBaixo.getHeight() - espacoCanos / 2 + alturaCanosRandom);
            //Desenha Passaro
            batch.draw(passaro[(int) animacaoPassaro], 120, posicaoInicialY);
            //Desenha a pontuação
            fontePontuacao.draw(batch,String.valueOf(pontuacao),eixoX / 2, eixoY - 50);
            if(gameState == 2) {
                batch.draw(gameOver, eixoX / 2 - gameOver.getWidth() / 2, eixoY / 2 + gameOver.getWidth() / 2);
                fontePontuacao.draw(batch,"Toque para reiniciar",eixoX / 6 , eixoY / 2);
            }
            batch.end();

            circuloPassaro.set(120 + passaro[0].getWidth() / 2, posicaoInicialY + passaro[0].getHeight() / 2, 30);

         rectangleCanoCima = new Rectangle(
                 posicaoHorizontalCano - 100,
                 eixoY / 2 + espacoCanos / 2 + alturaCanosRandom,
                 canoCima.getWidth(),
                 canoCima.getHeight()
         );
         rectangleCanoBaixo = new Rectangle(
                 posicaoHorizontalCano - 100, eixoY / 2 - canoBaixo.getHeight() - espacoCanos / 2 + alturaCanosRandom, canoBaixo.getWidth(),canoBaixo.getHeight()
         );

         // --
            //Desenhar circles e rectangles

           /* shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.circle(circuloPassaro.x, circuloPassaro.y, circuloPassaro.radius);
            shape.setColor(Color.GREEN);
            shape.rect(rectangleCanoCima.x, rectangleCanoCima.y, rectangleCanoCima.width, rectangleCanoCima.height);
            shape.rect(rectangleCanoBaixo.x, rectangleCanoBaixo.y, rectangleCanoBaixo.width, rectangleCanoBaixo.height);
            shape.setColor(Color.ORANGE);
            shape.end();
            */


            // Teste Colisão
         if(Intersector.overlaps(circuloPassaro, rectangleCanoBaixo) || Intersector.overlaps(circuloPassaro, rectangleCanoCima)
                 || posicaoInicialY <= 0 || posicaoInicialY >= eixoY){


             Gdx.app.log("Colision", "Colidiu");
             gameState = 2;
         }


        }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
	
	/* @Override
	public void dispose () {
		batch.dispose();
		passaro.dispose();
    */


