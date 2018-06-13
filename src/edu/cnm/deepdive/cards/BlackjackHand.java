package edu.cnm.deepdive.cards;

import edu.cnm.deepdive.cards.Deck.InsufficientCardsException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class implements (partially) a Blackjack hand, supporting drawing cards
 * from a {@link Deck}, evaluating the cards in the hand, and comparing one hand
 * to another. There's also an abstract method ({@link #play()}) intended for
 * concrete subclasses to implement strategy and game play interaction.
 *
 * @author Nicholas Bennett &amp; Deep Dive Coding Java Cohort 4
 */
public abstract class BlackjackHand implements Comparable<BlackjackHand> {

  private static final int[] VALUES = {
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      10,
      10,
      10,
  };
  private static final String RESOURCE_BUNDLE = "resources/blackjack_hand";

  public static final String BUSTED_PATTERN_KEY = "busted_pattern";
  public static final String BLACKJACK_PATTERN_KEY = "blackjack_pattern";
  public static final String POINTS_PATTERN_KEY = "points_pattern";
  public static final String HARD_POINTS_PATTERN_KEY = "hard_points_pattern";
  public static final String TO_STRING_PATTERN_KEY = "to_string_pattern";

  private static ResourceBundle bundle;

  private List<Card> hand;
  private Deck deck;
  private boolean soft;
  private boolean blackjack;
  private boolean busted;
  private int value;
  private int total;

  static {
    bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
  }

  /**
   * Initializes this instance with the specified {@link Deck}. 2 cards are
   * immediately drawn from this deck, and the same deck is used for subsequent
   * hits.
   *
   * @param deck                        source from which cards are drawn for
   *                                    the hand.
   * @throws InsufficientCardsException if <code>deck</code> doesn't have at
   *                                    least 2 undealt cards available.
   */
  protected BlackjackHand(Deck deck) throws InsufficientCardsException {
    Card[] cards = deck.deal(2);
    hand = new LinkedList<>();
    hand.addAll(Arrays.asList(cards));
    this.deck = deck;
    update();
  }

  private void update() {
    boolean aceInHand = false;
    total = 0;
    soft = false;
    blackjack = false;
    busted = false;
    for (Card card : hand) {
      if (card.getRank() == Rank.ACE) {
        aceInHand = true;
      }
      total += VALUES[card.getRank().ordinal()];
    }
    if (total > 21) {
      busted = true;
      value = 0;
    } else if (total <= 11 && aceInHand) {
      total += 10;
      soft = true;
      if (hand.size() == 2 && total == 21) {
        blackjack = true;
      }
      value = total;
    } else {
      value = total;
    }
  }

  /**
   * Draws a card from the {@link Deck} provided to the {@link
   * #BlackjackHand(Deck)} constructor and adds it the cards contained in this
   * instance.
   *
   * @throws InsufficientCardsException if the deck doesn't have at least 1
   *                                    undealt card available.
   */
  protected final void hit() throws InsufficientCardsException {
    hand.add(deck.deal());
    update();
  }

  /**
   * Compares this instance to another instance of this class (or a subclass),
   * based first on the total value of the cards in the hand, where a busted
   * hand has a value of 0. If both hands have a value of 21, but only one is a
   * blackjack, that hand is considered to be of higher value. Note that this
   * comparison does not take into account whether one hand or the other is the
   * dealer's hand.
   *
   * @param other   hand to which this instance will be compared.
   * @return        negative if this instance is beaten by <code>other</code>,
   *                positive if the reverse is true, and zero if the 2 hands
   *                "push" (are equal).
   */
  @Override
  public int compareTo(BlackjackHand other) {
    int comparison = Integer.compare(value, other.value);
    if (comparison == 0) {
      if (blackjack && !other.blackjack) {
        comparison = 1;
      } else if (other.blackjack && !blackjack) {
        comparison = -1;
      }
    }
    return comparison;
  }

  /**
   * Returns a flag indicating whether this instance's {@link #getValue()}
   * incorporates an ace counting as 11 points (aka a "soft ace").
   *
   * @return    flag indicating a soft ace used in computing this instance's
   *            value.
   */
  protected boolean isSoft() {
    return soft;
  }

  /**
   * In the event that this instance has a value (as returned by {@link
   * #getValue()}) of 21, returns a flag indicating whether that value was
   * obtained by the combination of an ace (specifically, a soft ace) with a
   * face card or 10. If that is the case, then the hand is a Blackjack.
   *
   * @return    flag indicating that this instance constitutes a Blackjack.
   */
  public boolean isBlackjack() {
    return blackjack;
  }

  /**
   * Returns a flag indicating whether this instance is "busted" &ndash; that
   * is, the cards add up to more than 21, even when any aces in the hand are
   * treated as "hard" (1 point each). If this is the case, the value returned
   * by {@link #getValue()} is 0.
   *
   * @return    flag indicating that the cards in this instance add up to a
   *            total exceeding 21.
   */
  public boolean isBusted() {
    return busted;
  }

  /**
   * Returns the value of this instance, where a busted hand has a value of 0.
   *
   * @return    value of the hand.
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the point total of this instance, which may exceed 21 (in the event
   * of a busted hand).
   *
   * @return    point total of the cards in the hand.
   */
  public int getTotal() {
    return total;
  }

  /**
   * Returns all of the {@link Card Cards} in this instance, copied to an array.
   * Note that modifications to the array returned will not affect the hand
   * contents or value.
   *
   * @return    contents of the hand.
   */
  public Card[] getHand() {
    return hand.toArray(new Card[hand.size()]);
  }

  /**
   * In a concrete subclass, this method implements that class's play strategy
   * and any required user interaction. When this method returns, it is assumed
   * that play of the hand has completed &ndash; that is, that the total of the
   * cards in the hand has reached or exceeded 21, or the implemented
   * "player"/strategy has chosen to stand on a lower total.
   *
   * @throws InsufficientCardsException   in the event that the {@link Deck}
   *                                      specified in the constructor invocation
   *                                      runs out of cards during play.
   */
  public abstract void play() throws InsufficientCardsException;

  @Override
  public String toString() {
    String status;
    if (isBusted()) {
      status = String.format(bundle.getString(BUSTED_PATTERN_KEY), total);
    } else if (isBlackjack()) {
      status = bundle.getString(BLACKJACK_PATTERN_KEY);
    } else if (isSoft()) {
      status = String.format(bundle.getString(POINTS_PATTERN_KEY), total - 10, total);
    } else {
      status = String.format(bundle.getString(HARD_POINTS_PATTERN_KEY), total);
    }
    return String.format(bundle.getString(TO_STRING_PATTERN_KEY), Arrays.toString(getHand()), status);
  }

}
