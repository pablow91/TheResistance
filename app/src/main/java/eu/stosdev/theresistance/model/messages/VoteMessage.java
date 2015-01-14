package eu.stosdev.theresistance.model.messages;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class VoteMessage<T extends AbsEvent> extends AbsMessage<T> {
}
