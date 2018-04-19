package com.minelife.tdm;

import net.minecraftforge.fml.common.eventhandler.Event;

public class TDMEvent extends Event {

    private Match match;

    public TDMEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public class StartEvent extends TDMEvent {

        public StartEvent(Match match) {
            super(match);
        }

    }

    public class WinEvent extends TDMEvent {

        public WinEvent(Match match) {
            super(match);
        }

    }


    public class MatchForcefullyEnded extends TDMEvent {

        public MatchForcefullyEnded(Match match) {
            super(match);
        }

    }
}
