package com.endless.model;

import java.util.*;

/**
 * Manages the play queue, playlist items, shuffle state, and repeat behaviors.
 */
public class Playlist {

    public enum RepeatMode {
        OFF, ALL, ONE
    }

    public interface PlaylistListener {
        void onPlaylistChanged();
        void onCurrentTrackChanged(Track track, int index);
        void onShuffleChanged(boolean shuffle);
        void onRepeatChanged(RepeatMode mode);
    }

    private final List<Track> tracks = new ArrayList<>();
    private final List<Integer> shuffleIndices = new ArrayList<>();
    private int shuffleCursor = -1;
    private int currentIndex = -1;

    private boolean shuffle = false;
    private RepeatMode repeatMode = RepeatMode.OFF;

    private final List<PlaylistListener> listeners = new ArrayList<>();

    public void addListener(PlaylistListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlaylistListener listener) {
        listeners.remove(listener);
    }

    public synchronized void addTrack(Track track) {
        if (track != null) {
            tracks.add(track);
            rebuildShuffle();
            notifyPlaylistChanged();
        }
    }

    public synchronized void addTracks(List<Track> newTracks) {
        if (newTracks != null && !newTracks.isEmpty()) {
            tracks.addAll(newTracks);
            rebuildShuffle();
            notifyPlaylistChanged();
        }
    }

    public synchronized void removeTrack(int index) {
        if (index >= 0 && index < tracks.size()) {
            boolean wasCurrent = (index == currentIndex);
            tracks.remove(index);
            if (wasCurrent) {
                if (tracks.isEmpty()) {
                    currentIndex = -1;
                } else if (currentIndex >= tracks.size()) {
                    currentIndex = 0;
                }
            } else if (index < currentIndex) {
                currentIndex--;
            }
            rebuildShuffle();
            notifyPlaylistChanged();
            if (wasCurrent) {
                notifyCurrentTrackChanged();
            }
        }
    }

    public synchronized void clear() {
        tracks.clear();
        shuffleIndices.clear();
        currentIndex = -1;
        shuffleCursor = -1;
        notifyPlaylistChanged();
        notifyCurrentTrackChanged();
    }

    public synchronized List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    public synchronized int getTrackCount() {
        return tracks.size();
    }

    public synchronized Track getTrack(int index) {
        if (index >= 0 && index < tracks.size()) {
            return tracks.get(index);
        }
        return null;
    }

    public synchronized int getCurrentIndex() {
        return currentIndex;
    }

    public synchronized Track getCurrentTrack() {
        if (currentIndex >= 0 && currentIndex < tracks.size()) {
            return tracks.get(currentIndex);
        }
        return null;
    }

    public synchronized void setCurrentIndex(int index) {
        if (index >= 0 && index < tracks.size()) {
            this.currentIndex = index;
            // Align shuffle cursor
            if (shuffle) {
                int pos = shuffleIndices.indexOf(index);
                if (pos >= 0) {
                    shuffleCursor = pos;
                }
            }
            notifyCurrentTrackChanged();
        }
    }

    public synchronized Track next() {
        if (tracks.isEmpty()) return null;

        if (repeatMode == RepeatMode.ONE && currentIndex >= 0) {
            notifyCurrentTrackChanged();
            return getCurrentTrack();
        }

        if (shuffle) {
            if (shuffleIndices.isEmpty()) rebuildShuffle();
            shuffleCursor++;
            if (shuffleCursor >= shuffleIndices.size()) {
                if (repeatMode == RepeatMode.ALL) {
                    rebuildShuffle();
                    shuffleCursor = 0;
                } else {
                    shuffleCursor = shuffleIndices.size() - 1;
                    return null; // Queue ended
                }
            }
            currentIndex = shuffleIndices.get(shuffleCursor);
        } else {
            if (currentIndex + 1 < tracks.size()) {
                currentIndex++;
            } else if (repeatMode == RepeatMode.ALL) {
                currentIndex = 0;
            } else {
                return null; // Queue ended
            }
        }

        notifyCurrentTrackChanged();
        return getCurrentTrack();
    }

    public synchronized Track previous() {
        if (tracks.isEmpty()) return null;

        if (shuffle) {
            if (shuffleCursor > 0) {
                shuffleCursor--;
                currentIndex = shuffleIndices.get(shuffleCursor);
            } else if (repeatMode == RepeatMode.ALL && !shuffleIndices.isEmpty()) {
                shuffleCursor = shuffleIndices.size() - 1;
                currentIndex = shuffleIndices.get(shuffleCursor);
            }
        } else {
            if (currentIndex > 0) {
                currentIndex--;
            } else if (repeatMode == RepeatMode.ALL) {
                currentIndex = tracks.size() - 1;
            }
        }

        notifyCurrentTrackChanged();
        return getCurrentTrack();
    }

    public synchronized boolean isShuffle() {
        return shuffle;
    }

    public synchronized void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if (shuffle) {
            rebuildShuffle();
        }
        notifyShuffleChanged();
    }

    public synchronized void toggleShuffle() {
        setShuffle(!shuffle);
    }

    public synchronized RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public synchronized void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
        notifyRepeatChanged();
    }

    public synchronized void cycleRepeatMode() {
        switch (repeatMode) {
            case OFF:
                setRepeatMode(RepeatMode.ALL);
                break;
            case ALL:
                setRepeatMode(RepeatMode.ONE);
                break;
            case ONE:
                setRepeatMode(RepeatMode.OFF);
                break;
        }
    }

    private void rebuildShuffle() {
        shuffleIndices.clear();
        for (int i = 0; i < tracks.size(); i++) {
            shuffleIndices.add(i);
        }
        Collections.shuffle(shuffleIndices);
        if (currentIndex >= 0) {
            shuffleCursor = shuffleIndices.indexOf(currentIndex);
        } else {
            shuffleCursor = -1;
        }
    }

    private void notifyPlaylistChanged() {
        for (PlaylistListener l : listeners) {
            l.onPlaylistChanged();
        }
    }

    private void notifyCurrentTrackChanged() {
        Track track = getCurrentTrack();
        int idx = currentIndex;
        for (PlaylistListener l : listeners) {
            l.onCurrentTrackChanged(track, idx);
        }
    }

    private void notifyShuffleChanged() {
        for (PlaylistListener l : listeners) {
            l.onShuffleChanged(shuffle);
        }
    }

    private void notifyRepeatChanged() {
        for (PlaylistListener l : listeners) {
            l.onRepeatChanged(repeatMode);
        }
    }
}
