import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Fishing extends JPanel implements ActionListener {

    private int boardWidth;
    private int boardHeight;
    private boolean readyToCast = true;
    private boolean fishIsCaught = false;
    private Timer timer;
    private Random random;
    private String[] fishTypes = {"Largemouth Bass", "Smallmouth Bass", "Northern Pike", "Blue Catfish", "Trout", "Channel Catfish", "Salmon", "Bluegill", "Yellow Perch" };
    private String[] fishSizes = {"1", "2", "4", "5", "8", "10", "12", "14"}; // Fish sizes in pounds
    private int score = 0;
    private String currentMessage = "Welcome to the Fishing Game!";

    
    // Fish-related graphics
    private String caughtFish = "";
    private int caughtFishSize = 0;
    private boolean fishOnHook = false; // Flag to show a fish on the hook
    private boolean inCutscene = false; // Flag for cutscene

    // PNG for the caught fish cutscene
    private Image caughtFishImage;

    // New flag to track if the player has cast the line
    private boolean lineCast = false;

    private JButton castButton;
    private JButton hookButton;

    public Fishing(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.random = new Random();

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.CYAN);  // Set a blue sky background

        // Start a timer that updates every 1000ms (1 second)
        timer = new Timer(1000, this);
        timer.start();

        // Setup the buttons
        setLayout(null);  // Use null layout for custom positioning
        castButton = new JButton("Cast");
        castButton.setBounds(50, boardHeight - 80, 100, 30);
        castButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                castsLine();

            }

        });
        add(castButton);

        hookButton = new JButton("Hook");
        hookButton.setBounds(200, boardHeight - 80, 100, 30);
        hookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hookFish();
            }
        });
        add(hookButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // Draw sky
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, boardWidth, boardHeight); // Fill the sky
    
        // Draw the sun
        g.setColor(Color.YELLOW);
        g.fillOval(boardWidth - 150, 30, 100, 100); // Sun at the top-right corner
    
        // Clouds 
        g.setColor(Color.WHITE);
        g.fillOval(80, 50, 180, 70);  // Cloud 1 
        g.fillOval(120, 40, 150, 60);  // Cloud 2 
        g.fillOval(200, 80, 160, 70);  // Cloud 3
        g.fillOval(400, 60, 150, 60);  // Cloud 4 
        g.fillOval(350, 120, 180, 70); // Cloud 5 
    
        // Draw birds in the distance (simple lines)
        g.setColor(Color.BLACK);
        g.drawLine(100, 120, 120, 110); // Bird 1 
        g.drawLine(120, 110, 140, 120);
        g.drawLine(300, 150, 320, 140); // Bird 2
        g.drawLine(320, 140, 340, 150);
    
        // Draw the water
        g.setColor(Color.BLUE);
        g.fillRect(0, boardHeight / 2, boardWidth, boardHeight / 2); // Bottom half of the screen is water
    
        // Draw fisherman with improved aesthetics
        drawFisherman(g);
    
        // Draw the boat under the fisherman
        drawBoat(g);
    
        // If in cutscene, display the caught fish image
        if (inCutscene && caughtFishImage != null) {
            g.drawImage(caughtFishImage, boardWidth / 2 - 150, boardHeight / 2 - 100, 300, 200, this);
        } else {
            // Draw fish on the hook
            if (fishOnHook) {
                g.setColor(Color.GREEN); // Fish color
                g.fillOval(boardWidth / 2 + 90, boardHeight / 2 + 90, 40, 20); // Fish shape (oval)
            
                // Set larger font size and change text color for emphasis
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 36)); // Larger and bold font
                FontMetrics metrics = g.getFontMetrics();  // To calculate width of the text for centering
                String message = "Fish on the hook!";
                int messageWidth = metrics.stringWidth(message);
                int messageHeight = metrics.getHeight();
            
                // Position the text above the fisherman's head
                int fishermanHeadY = boardHeight / 2 - 140;  // Y-coordinate for the fisherman's head
                g.drawString(message, (boardWidth - messageWidth) / 2, fishermanHeadY - 20); // Text above head
            }
    
            // Draw fish in the water randomly
            drawFishInWater(g);
    
            // Draw the game messages
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(currentMessage, 50, 50);
    
            // Display caught fish information
            if (!caughtFish.isEmpty() && !inCutscene) {
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("You caught a " + caughtFishSize + " lb " + caughtFish + "!", 50, 100);
                g.drawString("Current Score: " + score, 50, 150);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (lineCast && !inCutscene) {
            fishAtSpot();  // Check if the fish bites only if the line has been cast
        }
        repaint();
    }

    public void castsLine() {
        if (readyToCast) {
            lineCast = true;  // Set lineCast to true when the line is cast
            readyToCast = false;  // Disable further casts until the current one is complete
            System.out.println("You cast the line into the water.");
       
        } else {
            System.out.println("You're not ready to cast yet.");
        }
    }

    public void gameLoop() {
        // If the player catches a fish
        if (fishIsCaught) {
            catchFish();  // Handle catching the fish and resetting the game state
        }
    
    
    }
    

    public void hookFish() {
        if (fishOnHook) {
            catchFish();
        } else {
            currentMessage = "No fish on the hook!";
        }
    }

    public void fishAtSpot() {
        currentMessage = "You cast your line... waiting for a bite...";
    
        // Simulate a longer wait time by using a random chance
        if (random.nextInt(10) > 3) { // Make it less likely to bite (e.g., 70% chance no bite)
            fishOnHook = false; // No fish
            currentMessage = "The fish got away!";
        } else {
            fishOnHook = true; // Fish caught on hook
            currentMessage = "A fish is biting!";
        }
    }

    public void catchFish() {
        if (lineCast) {  // Ensure the line was cast before catching a fish
            // Randomly determine the fish type and size
            String caughtFishType = getRandomFishType();
            String caughtFishSizeStr = getRandomFishSize(); // Keep the size as a string for later display
            int caughtFishSizeInt = Integer.parseInt(caughtFishSizeStr); // Convert to integer for logic, if needed
    
            // Log the caught fish details
            System.out.println("Caught a " + caughtFishSizeInt + " " + caughtFishType + "!");
            
            // Update the variables
            caughtFish = caughtFishType;  // Set the fish type
            caughtFishSize = caughtFishSizeInt;  // Set the fish size
            
            score += caughtFishSizeInt;  // Add the fish size to the player's score
    
            startCutscene();
            fishIsCaught = true;
    
            // Reset fishing state
            lineCast = false;  // The line is no longer in the water
            resetFishingState();  // Allow player to cast again
        }
    }

    private String getRandomFishType() {
        int index = (int) (Math.random() * fishTypes.length);
        return fishTypes[index];
    }

    private String getRandomFishSize() {
        int index = (int) (Math.random() * fishSizes.length);
        return fishSizes[index];
    }

    public void resetFishingState() {
        // Reset fishIsCaught when ready to fish again
        fishIsCaught = false;
        readyToCast = true;  // Allow the player to cast again
        System.out.println("Fishing state reset. Ready to cast again.");
        // Other reset logic as needed (e.g., reset visuals)
    }

    private void startCutscene() {
        fishOnHook = false;  // Hide fish on hook during cutscene
        inCutscene = true;
        currentMessage = "Cutscene: You caught a " + caughtFish + "!";

        // Load the PNG image of the caught fish (e.g., "bass.png" or "trout.png")
        try {
            caughtFishImage = ImageIO.read(new File(caughtFish.toLowerCase() + ".png")); // File name should match fish name (bass.png, trout.png, etc.)
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Simulate a short cutscene duration, then end the cutscene
        Timer cutsceneTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inCutscene = false;  // End cutscene
            }
        });
        cutsceneTimer.setRepeats(false);  // Only trigger once
        cutsceneTimer.start();
    }

    private void drawBoat(Graphics g) {
        // Boat hull 
        g.setColor(new Color(139, 69, 19)); // Brown color for the hull (wooden boat)
        g.fillRoundRect(boardWidth / 2 - 100, boardHeight / 2 - 40, 200, 30, 20, 20); // Move the boat down
    
        // Boat bottom 
        g.setColor(new Color(101, 67, 33)); // Darker brown for the bottom
        g.fillRoundRect(boardWidth / 2 - 100, boardHeight / 2 - 10, 200, 10, 10, 10); // Bottom portion of the boat
    
        // Boat interior 
        g.setColor(new Color(222, 184, 135)); // Light brown color for the interior
        g.fillRect(boardWidth / 2 - 90, boardHeight / 2 - 35, 180, 10); // Interior floor
    
        // Boat railing 
        g.setColor(new Color(0, 0, 0)); // Black color for the railing
        g.fillRect(boardWidth / 2 - 100, boardHeight / 2 - 45, 200, 5); // Top railing of the boat
    
        // Add steering wheel 
        g.setColor(new Color(160, 82, 45)); // Steering wheel color (wooden)
        g.fillOval(boardWidth / 2 - 15, boardHeight / 2 - 25, 30, 30); // Steering wheel
    
        // Add boat seats 
        g.setColor(new Color(222, 184, 135)); // Seat color (light wood)
        g.fillRect(boardWidth / 2 - 50, boardHeight / 2 - 35, 40, 10); // Seat on the left
        g.fillRect(boardWidth / 2 + 10, boardHeight / 2 - 35, 40, 10); // Seat on the right
    
        // Boat anchor 
        g.setColor(new Color(169, 169, 169)); // Gray color for anchor
        g.fillRect(boardWidth / 2 + 80, boardHeight / 2 - 20, 10, 15); // Vertical part of the anchor
        g.fillRect(boardWidth / 2 + 75, boardHeight / 2 - 5, 20, 5); // Horizontal part of the anchor
    }

    private void drawFishInWater(Graphics g) {
    // Only draw fish if no fish is on the hook
    if (!fishOnHook) {
        // Draw fish randomly in the water
        g.setColor(Color.GREEN);
        for (int i = 0; i < 5; i++) {
            int fishX = random.nextInt(boardWidth);
            int fishY = random.nextInt(boardHeight / 2) + boardHeight / 2;
            g.fillOval(fishX, fishY, 30, 15);  // Draw fish as ovals
        }
    }
}

    public void drawFisherman(Graphics g) {
        // Fisherman body
        g.setColor(Color.ORANGE);
        g.fillRect(boardWidth / 2 - 50, boardHeight / 2 - 100, 30, 70); // Body
        
        // Fisherman head 
        g.setColor(Color.PINK);
        g.fillOval(boardWidth / 2 - 60, boardHeight / 2 - 140, 50, 50); // Head
        
        // Face details 
        g.setColor(Color.BLACK);
        g.fillOval(boardWidth / 2 - 45, boardHeight / 2 - 125, 10, 10); // Left eye
        g.fillOval(boardWidth / 2 - 25, boardHeight / 2 - 125, 10, 10); // Right eye
        g.drawArc(boardWidth / 2 - 40, boardHeight / 2 - 110, 30, 10, 0, -180); // Mouth
        
        // Hat on the fisherman
        g.setColor(Color.GRAY); 
        g.fillRect(boardWidth / 2 - 60, boardHeight / 2 - 140, 50, 20); // Hat brim
        g.fillRect(boardWidth / 2 - 45, boardHeight / 2 - 155, 20, 15); // Hat top
        
        // Fisherman arms
        g.setColor(Color.ORANGE);
        g.fillRect(boardWidth / 2 - 80, boardHeight / 2 - 80, 60, 10); // Left arm
        g.fillRect(boardWidth / 2 - 50, boardHeight / 2 - 80, 60, 10); // Right arm 
        
        // Draw the fishing rod 
        g.setColor(Color.red); // Color of the fishing rod
        int rodX = boardWidth / 2 + 20; 
        int rodY = boardHeight / 2 - 70; 
        int rodLength = 80; // Length of the rod
        g.fillRect(rodX, rodY - rodLength, 5, rodLength); // Vertical rod
        
        // Draw the reel (
        g.setColor(Color.GRAY);
        g.fillOval(rodX - 10, rodY - 20, 20, 20); 
        
        // Draw the reel handle 
        g.setColor(Color.DARK_GRAY);
        g.fillRect(rodX + 10, rodY - 10, 10, 3); // Handle on the reel (small horizontal line)
        
        // Only draw the fishing line if the line is casted
        if (lineCast) {
            g.setColor(Color.BLACK);
            int lineStartX = rodX + 2;
            int lineStartY = rodY - rodLength;
            int lineEndX = rodX + 180;
            int lineEndY = boardHeight / 2 + 100;
            g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY); // Line from rod to the water
        }
    }
}