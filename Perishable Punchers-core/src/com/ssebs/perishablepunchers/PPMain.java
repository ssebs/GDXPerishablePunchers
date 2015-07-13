package com.ssebs.perishablepunchers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Perishable Punchers Game developed by ssebs
 * 
 * @author ssebs
 */

// TODO: add walking animations, redesign art, health of both characters, diff
// characters/maps, and a config file for having the sound on or off
// also ad fireball button in center. maybe shield

public class PPMain extends ApplicationAdapter
{
	final int WIDTH = 1280;
	final int HEIGHT = 720;

	Preferences preferences;

	SpriteBatch batch;
	Texture background, menu, controls, backButton, attackButton, jumpButton, leftButton, rightButton, charSelect;
	Texture enemyAttack, fighter, enemy, fighterKunch, explosion, fireBallButton, mute, sound;
	OrthographicCamera camera;
	Rectangle player, npc;

	int x = 256 + 64, y = 64, ex = WIDTH - (256 + 64), wy = 64, fighterHealth = 100, enemyHealth = 100;
	int fbX, fbY;// fireball coords

	private static final int FRAME_COLS = 2;
	private static final int FRAME_ROWS = 2;

	Animation walkAnimation, walkAnimation2, p1WalkAnimationR, p1WalkAnimationL;
	Texture walkSheet, walkSheet2, p1WalkRSheet, p1WalkLSheet;
	TextureRegion[] walkFrames, walkFrames2, p1WalkRFrames, p1WalkLFrames;
	TextureRegion currentFrame, currentFrame2, p1WalkRFrame, p1WalkLFrame;
	float stateTime, stateTime2, stateTime3, stateTime4;

	boolean isMovingLeft = false;
	boolean isMovingRight = false;
	boolean kunch = false;
	boolean clicking = false;
	boolean overlapping = false;
	boolean isPressingButton = false;
	boolean isPressingButton2 = false;
	boolean ableToJump = true;
	boolean firstTimePlaying = true;
	boolean enemyAttacking = false;
	boolean debugging = false;
	boolean renderFireBall = false;
	boolean isPlayerMovingRight = false;
	boolean isPlayerMovingLeft = false;

	// boolean isSoundOn = true;// THIS SHOULD BE A SETTING
	boolean isSoundOn;

	GestureDetector gestureDetector;
	MyGestureListener myGestureListener;

	Sound jumpSound, hitSound, fireBallSound;

	GameState gameState = GameState.MENU;
	CharacterColor characterColor = CharacterColor.PINK;

	enum GameState
	{
		MENU, PLAY, CONTROLS, CHARACTER_SELECTION;
	}

	enum CharacterColor
	{
		PINK, RED, GREEN, BLUE;
	}

	@Override
	public void create()
	{

		jumpSound = Gdx.audio.newSound(Gdx.files.internal("data/Jump.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("data/Punch.wav"));
		fireBallSound = Gdx.audio.newSound(Gdx.files.internal("data/Haduken.wav"));
		{
			walkSheet = new Texture(Gdx.files.internal("data/DargonAnimRight.png"));
			TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS);
			walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					walkFrames[index++] = tmp[i][j];
				}
			}
			walkAnimation = new Animation(0.25f, walkFrames);
			stateTime = 0f;
			// dragon right
		}
		{
			walkSheet2 = new Texture(Gdx.files.internal("data/DargonAnimLeft.png"));
			TextureRegion[][] tmp2 = TextureRegion.split(walkSheet2, walkSheet2.getWidth() / FRAME_COLS, walkSheet2.getHeight() / FRAME_ROWS);
			walkFrames2 = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index2 = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					walkFrames2[index2++] = tmp2[i][j];
				}
			}
			walkAnimation2 = new Animation(0.25f, walkFrames2);
			stateTime2 = 0f;
			// dragon left

		}

		{
			p1WalkLSheet = new Texture(Gdx.files.internal("data/Player1WalkAnimL.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p1WalkLSheet, p1WalkLSheet.getWidth() / FRAME_COLS, p1WalkLSheet.getHeight() / FRAME_ROWS); // #10
			p1WalkLFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p1WalkLFrames[index++] = tmp[i][j];
				}
			}
			p1WalkAnimationL = new Animation(0.15f, p1WalkLFrames); // #11
			stateTime3 = 0f;
			// player1Left
		}
		{
			p1WalkRSheet = new Texture(Gdx.files.internal("data/Player1WalkAnimR.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p1WalkRSheet, p1WalkRSheet.getWidth() / FRAME_COLS, p1WalkRSheet.getHeight() / FRAME_ROWS); // #10
			p1WalkRFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p1WalkRFrames[index++] = tmp[i][j];
				}
			}
			p1WalkAnimationR = new Animation(0.15f, p1WalkRFrames); // #11
			stateTime4 = 0f;
			// player1Right
		}

		/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * */
		background = new Texture(Gdx.files.internal("data/BGHD.png"));
		menu = new Texture(Gdx.files.internal("data/Menu.png"));
		controls = new Texture(Gdx.files.internal("data/Controls.png"));
		charSelect = new Texture(Gdx.files.internal("data/CharacterSelection.png"));

		backButton = new Texture(Gdx.files.internal("data/Back.png"));
		attackButton = new Texture(Gdx.files.internal("data/Attack.png"));
		jumpButton = new Texture(Gdx.files.internal("data/Jump.png"));
		leftButton = new Texture(Gdx.files.internal("data/Left.png"));
		rightButton = new Texture(Gdx.files.internal("data/Right.png"));
		fireBallButton = new Texture(Gdx.files.internal("data/FireBallButton.png"));
		mute = new Texture(Gdx.files.internal("data/Mute.png"));
		sound = new Texture(Gdx.files.internal("data/Sound.png"));

		enemy = new Texture(Gdx.files.internal("data/DargonWalkHD.png"));
		enemyAttack = new Texture(Gdx.files.internal("data/DargonKunchHD.png"));
		explosion = new Texture(Gdx.files.internal("data/ExplosionHD75Opacity.png"));

		player = new Rectangle(x, y, 256, 256);
		npc = new Rectangle(ex, wy, 256, 256);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();

		myGestureListener = new MyGestureListener();
		gestureDetector = new GestureDetector(myGestureListener);

		Gdx.input.setInputProcessor(gestureDetector);
		Gdx.input.setCatchBackKey(true);

		preferences = Gdx.app.getPreferences("Perishable Punchers Settings.prefs");
		isSoundOn = preferences.getBoolean("isSoundOn");
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		Gdx.graphics.setTitle("Perishable Punchers|FPS:" + Gdx.graphics.getFramesPerSecond());
		switch (gameState)
		{
		case MENU:
			renderMenu();
			break;
		case PLAY:
			renderGame();
			break;
		case CONTROLS:
			renderControls();
			break;
		case CHARACTER_SELECTION:
			renderCharSelect();
			break;
		default:
			break;
		}
	}

	private void renderCharSelect()
	{
		batch.begin();
		batch.draw(charSelect, 0, 0);
		batch.end();
		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
		}
		// to here is so the display coords work
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 210 && touchPos.x < 435)
			{
				if (touchPos.y > 360 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top left
						System.out.println("TOP LEFT");
					}
					characterColor = CharacterColor.PINK;
					loadCharTexture(characterColor);
					firstTimePlaying = false;
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 850 && touchPos.x < 1075)
			{
				if (touchPos.y > 360 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top right
						System.out.println("TOP RIGHT");
					}
					characterColor = CharacterColor.RED;
					loadCharTexture(characterColor);
					firstTimePlaying = false;
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 210 && touchPos.x < 435)
			{
				if (touchPos.y > 64 && touchPos.y < 330)
				{
					if (debugging)
					{
						// bott left
						System.out.println("BOTTOM LEFT");
					}
					characterColor = CharacterColor.GREEN;
					loadCharTexture(characterColor);
					firstTimePlaying = false;
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 850 && touchPos.x < 1075)
			{
				if (touchPos.y > 64 && touchPos.y < 330)
				{
					if (debugging)
					{
						// bott right
						System.out.println("BOTTOM RIGHT");
					}
					characterColor = CharacterColor.BLUE;
					loadCharTexture(characterColor);
					firstTimePlaying = false;
					gameState = GameState.PLAY;
				}
			}
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.MENU;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}

	}

	private void renderControls()
	{
		batch.begin();
		batch.draw(controls, 0, 0);
		batch.end();
		if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isButtonPressed(0))
		{
			try
			{
				Thread.sleep(250);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			gameState = GameState.MENU;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}
	}

	private void renderMenu()
	{
		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
			// to here is so the display coords work
		}
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 400 && touchPos.x < 875)
			{
				if (touchPos.y > 390 && touchPos.y < 545)
				{
					if (!firstTimePlaying)
					{
						// in play button

						gameState = GameState.PLAY;
					} else
					{
						gameState = GameState.CHARACTER_SELECTION;

					}
				}
			}
			if (touchPos.x > 475 && touchPos.x < 800)
			{
				if (touchPos.y > 225 && touchPos.y < 330)
				{
					// in controls button
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.CONTROLS;
				}
			}
			if (touchPos.x > 475 && touchPos.x < 800)
			{
				if (touchPos.y > 60 && touchPos.y < 170)
				{
					// in quit button
					dispose();
					System.exit(0);
				}
			}
			if (touchPos.x > 16 && touchPos.x < 64 + 16)
			{
				if (touchPos.y > 16 && touchPos.y < 64 + 16)
				{
					// in mute button
					isSoundOn = !isSoundOn;
					preferences.putBoolean("isSoundOn", isSoundOn);
					preferences.flush();
					try
					{
						Thread.sleep(100);
					} catch (Exception e)
					{
					}
				}
			}

		}
		batch.begin();
		batch.draw(menu, 0, 0);
		if (isSoundOn)
		{
			batch.draw(sound, 16, 16);
		} else
		{
			batch.draw(mute, 16, 16);
		}
		batch.end();

	}

	private void renderGame()
	{
		stateTime += Gdx.graphics.getDeltaTime(); // #15
		stateTime2 += Gdx.graphics.getDeltaTime(); // #15
		stateTime3 += Gdx.graphics.getDeltaTime(); // #15
		stateTime4 += Gdx.graphics.getDeltaTime(); // #15
		currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		currentFrame2 = walkAnimation2.getKeyFrame(stateTime2, true);
		p1WalkLFrame = p1WalkAnimationL.getKeyFrame(stateTime3, true);
		p1WalkRFrame = p1WalkAnimationR.getKeyFrame(stateTime4, true);

		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);

		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			if (y < 65)
			{
				jump();
			}
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			x -= 800 * Gdx.graphics.getDeltaTime();
			isPlayerMovingLeft = true;
			isPlayerMovingRight = false;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			x += 800 * Gdx.graphics.getDeltaTime();
			isPlayerMovingLeft = false;
			isPlayerMovingRight = true;
		} else
		{
			isPlayerMovingLeft = false;
			isPlayerMovingRight = false;
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
		{
			attack();
		}
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			shootFireBall();
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.MENU;
		}

		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}

		// ////////make touch left move guy left & vice versa,;
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 4 + 4 + 256 && touchPos.x < 4 + 4 + 256 + 256)
			{
				if (touchPos.y > 4 && touchPos.y < 4 + 128)
				{
					if (x > 16)
					{
						x -= 800 * Gdx.graphics.getDeltaTime();
						isPlayerMovingLeft = true;
						isPlayerMovingRight = false;
					}
				}
			} else if (touchPos.x > 765 && touchPos.x < 1015)
			{
				if (touchPos.y > 8 && touchPos.y < 8 + 128)
				{
					if (x < WIDTH - (256 + 16))
					{
						x += 800 * Gdx.graphics.getDeltaTime();
						isPlayerMovingLeft = false;
						isPlayerMovingRight = true;
					}
				}
			}
		}

		logic(touchPos.x, touchPos.y);

		batch.begin();
		batch.draw(background, 0, 0);
		if (!kunch)
		{
			// batch.draw(fighter, x, y);
			if (isPlayerMovingRight)
			{
				batch.draw(p1WalkRFrame, x, y);
			} else if (isPlayerMovingLeft)
			{
				batch.draw(p1WalkLFrame, x, y);
			} else
			{
				batch.draw(fighter, x, y);
			}

		} else
		{
			batch.draw(fighterKunch, x, y);
			if (overlapping)
			{
				if (isSoundOn)
				{
					hitSound.play();
				}
			}

		}

		if (isMovingRight)
		{
			batch.draw(currentFrame, ex, wy);
		} else if (isMovingLeft)
		{
			batch.draw(currentFrame2, ex, wy);
		} else
		{
			if (enemyAttacking)
			{
				batch.draw(enemyAttack, ex, wy);
			} else
			{
				batch.draw(enemy, ex, wy);
			}
		}

		if (overlapping)
		{
			// batch.draw(explosion, x - 128, y - 128);
		}
		// batch.draw(backButton, 4, HEIGHT - (64 + 4));

		if (renderFireBall)
		{
			batch.draw(explosion, fbX, fbY);
		}

		batch.draw(attackButton, 4, 4);
		batch.draw(leftButton, (256 + 4), 4);
		batch.draw(fireBallButton, WIDTH / 2 - (128), 4);
		batch.draw(jumpButton, WIDTH - (256 + 4), 4);
		batch.draw(rightButton, WIDTH - (512 + 4), 4);

		batch.end();
	}

	private void logic(float mX, float mY)
	{
		player.x = x;
		player.y = 64;
		npc.x = ex;

		if (Gdx.input.isButtonPressed(0))
		{
			if (mX > 4 && mX < 4 + 256)
			{
				if (mY > 4 && mY < 4 + 128)
				{
					// attack button
					isPressingButton = true;
					attack();
				}
			} else if (mX > 1025 && mX < 1272)
			{
				if (mY > 8 && mY < 4 + 122)
				{
					// jump button
					isPressingButton2 = true;

					if (y < 70)
					{
						jump();
					}
				}
			} else if (mX > 516 && mX < 764)
			{
				if (mY > 8 && mY < 4 + 122)
				{
					if (!renderFireBall)
					{
						// fireball button
						shootFireBall();
					}
				}
			} else
			{
				isPressingButton2 = false;
				isPressingButton = false;
			}

			if (mX > 4 && mX < 260)
			{
				if (mY > 650 && mY < 716)
				{
					// gameState = GameState.MENU;
				}
			}

		} else
		{
			clicking = false;
		}
		if (Gdx.input.isButtonPressed(1))
		{
			attack();
		} else
		{
			clicking = false;
		}

		dargonAI();

	}

	private void jump()
	{
		if (isSoundOn)
		{
			jumpSound.play();
		}
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				int speed = 8;
				while (y < 400)
				{

					y += 800 * Gdx.graphics.getDeltaTime();
					if (y < 256)
					{
						speed = 10;
					} else if (y > 300)
					{
						speed = 15;
					} else
					{
						speed = 15;
					}
					try
					{
						Thread.sleep(speed);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				while (y > 64)
				{
					y -= 600 * Gdx.graphics.getDeltaTime();

					if (y < 256)
					{
						speed = 10;
					} else if (y > 300)
					{
						speed = 15;
					} else
					{
						speed = 15;
					}
					try
					{
						Thread.sleep(speed);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (y > 64 && y < 16)
						y = 64;
				}

			}
		});
		thread.start();
	}

	private void attack()
	{

		if (!clicking)
		{
			kunch = true;
			if (npc.overlaps(player))
			{
				// deal damage

				Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						overlapping = true;
						// hitSound.play();
						try
						{
							Thread.sleep(250);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}

						overlapping = false;
					}
				});
				thread.start();
			} else
			{
				overlapping = false;
			}
		}

		clicking = true;
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				kunch = false;
			}
		});
		thread.start();
	}

	Thread thread2;

	private void shootFireBall()
	{

		// System.out.println("fireball method called");
		thread2 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				if (isSoundOn)
				{
					fireBallSound.play();
				}
			}
		});

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				renderFireBall = true;
				fbX = x;
				if (fbX < ex - 64)
				{
					thread2.start();
				} else if (fbX > ex + 64)
				{
					thread2.start();
				} else
				// touching
				{
				}
				do
				{
					if (fbX < ex - 64)
					{
						fbX += 1400 * Gdx.graphics.getDeltaTime();
					} else if (fbX > ex + 64)
					{
						fbX -= 1400 * Gdx.graphics.getDeltaTime();
					} else
					// touching
					{
						renderFireBall = false;
						fbX = x;
					}
					try
					{
						Thread.sleep(15);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} while (renderFireBall);
			}
		});
		thread.start();
	}

	private void dargonAI()
	{
		if (ex < x - 64)
		{
			isMovingRight = true;
			ex += 200 * Gdx.graphics.getDeltaTime();
			enemyAttacking = false;
		} else if (ex > x + 64)
		{
			isMovingLeft = true;
			ex -= 200 * Gdx.graphics.getDeltaTime();
			enemyAttacking = false;
		} else
		// touching
		{
			isMovingLeft = false;
			isMovingRight = false;
			int rand = (int) (Math.random() * 600);
			// System.out.println(rand);
			if (rand > 550)
			{
				enemyAttacking = true;
				if (isSoundOn)
				{
					// System.out.println("ENEMY HIT");
					hitSound.play();
				}
				Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							Thread.sleep(150);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						enemyAttacking = false;
					}
				});
				thread.start();
			} else
			{

			}
		}
	}

	private void loadCharTexture(CharacterColor color)
	{
		if (color == CharacterColor.PINK)
		{
			fighter = new Texture(Gdx.files.internal("data/Player1WalkHD.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player1KunchHD.png"));
		} else if (color == CharacterColor.RED)
		{
			fighter = new Texture(Gdx.files.internal("data/Player3WalkHD.png"));
			fighterKunch = new Texture(Gdx.files.internal("res/HD/Player3/Player3KunchHD.png"));
		} else if (color == CharacterColor.GREEN)
		{
			fighter = new Texture(Gdx.files.internal("data/Player2WalkHD.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player2KunchHD.png"));
		} else if (color == CharacterColor.BLUE)
		{
			fighter = new Texture(Gdx.files.internal("data/Player4WalkHD.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player4KunchHD.png"));
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();
		batch.dispose();
		background.dispose();
		if (!firstTimePlaying)
		{
			fighter.dispose();
		}
		firstTimePlaying = false;
	}

}
