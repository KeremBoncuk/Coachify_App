package io.coachify.messaging;

/** Central place for STOMP topic formats so we never hard-code strings. */
public final class ChatTopic {

  private ChatTopic() { }

  /** e.g.  /topic/chat/64f2… */
  public static String room(String roomIdHex) {
    return "/topic/chat/" + roomIdHex;
  }
}
