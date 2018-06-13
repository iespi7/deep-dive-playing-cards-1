package edu.cnm.deepdive.cards;

import edu.cnm.deepdive.cards.Deck.InsufficientCardsException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Concrete subclass of {@link BlackjackHand} support console-mode interaction
 * to allow a user to make game play decisions.
 *
 * @author Nicholas Bennett &amp; Deep Dive Coding Java Cohort 4
 */
public class InteractiveBlackjackHand extends BlackjackHand {

  private static final Pattern NON_WHITE_SPACE = Pattern.compile("\\S+");
  private static final String RESOURCE_BUNDLE = "resources/interactive_blackjack_hand";
  private static final String CURRENT_HAND_PATTERN_KEY = "current_hand_pattern";
  private static final String FINAL_HAND_PATTERN_KEY = "final_hand_pattern";
  private static final String ACTION_PROMPT_PATTERN_KEY = "action_prompt_pattern";
  private static final String YES_INPUT_CHAR_KEY = "yes_input_char";
  private static final String NO_INPUT_CHAR_KEY = "no_input_char";

  private static ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

  private Scanner scanner;

  /**
   * Initializes this instance with the specified {@link Deck} of cards and
   * {@link Scanner} for user input.
   *
   * @param deck                          source of cards for this instance.
   * @param scanner
   * @throws InsufficientCardsException   if <code>deck</code> doesn't have at
   *                                      least 2 undealt cards available.
   */
  public InteractiveBlackjackHand(Deck deck, Scanner scanner)
      throws InsufficientCardsException {
    super(deck);
    this.scanner = scanner;
  }

  /**
   * Interacts with the user, using {@link System#out} for output and a {@link
   * Scanner} for input, allowing the user to make hit/stay decisions until play
   * ends by the user staying, or the total of cards in the hand reaching or
   * exceeding 21.
   *
   * @throws InsufficientCardsException   in the event that the {@link Deck}
   *                                      specified in the constructor invocation
   *                                      runs out of cards during play.
   */
  @Override
  public void play() throws InsufficientCardsException {
    boolean stay = false;
    while (getTotal() < 21 && !stay) {
      System.out.printf(bundle.getString(CURRENT_HAND_PATTERN_KEY), this);
      Boolean hit = null;
      while (hit == null) {
        System.out.print(bundle.getString(ACTION_PROMPT_PATTERN_KEY));
        while (!scanner.hasNext(NON_WHITE_SPACE)) {}
        char input = scanner.next(NON_WHITE_SPACE).toLowerCase().charAt(0);
        if (input == bundle.getString(YES_INPUT_CHAR_KEY).charAt(0)) {
          hit = true;
          hit();
        } else if (input == bundle.getString(NO_INPUT_CHAR_KEY).charAt(0)) {
          hit = false;
          stay = true;
        }
        scanner.nextLine();
      }
    }
    if (!stay) {
      System.out.printf(bundle.getString(FINAL_HAND_PATTERN_KEY), this);
    }
  }

}
