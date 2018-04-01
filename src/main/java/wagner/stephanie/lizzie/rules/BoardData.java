package wagner.stephanie.lizzie.rules;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import wagner.stephanie.lizzie.analysis.MoveData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoardData {
    private ImmutablePair<Integer, Integer> boardSize;

    private Stone[] stonesOnBoard;
    private int[] lastMove;
    private Stone lastMoveColor;
    private boolean blackToPlay;
    private Zobrist zobrist;
    private int moveNumber;
    private int[] moveNumberListOnBoard;

    private List<VariationData> variationDataList;

    public BoardData(ImmutablePair<Integer, Integer> boardSize, Stone[] stonesOnBoard, int[] lastMove, Stone lastMoveColor, boolean blackToPlay, Zobrist zobrist, int moveNumber, int[] moveNumberListOnBoard, List<VariationData> variationDataList) {
        this.boardSize = boardSize;
        this.stonesOnBoard = stonesOnBoard;
        this.lastMove = lastMove;
        this.lastMoveColor = lastMoveColor;
        this.blackToPlay = blackToPlay;
        this.zobrist = zobrist;
        this.moveNumber = moveNumber;
        this.moveNumberListOnBoard = moveNumberListOnBoard;
        this.variationDataList = variationDataList;
    }

    public BoardData(ImmutablePair<Integer, Integer> boardSize, Stone[] stonesOnBoard, int[] lastMove, Stone lastMoveColor, boolean blackToPlay, Zobrist zobrist, int moveNumber, int[] moveNumberListOnBoard) {
        this(boardSize, stonesOnBoard, lastMove, lastMoveColor, blackToPlay, zobrist, moveNumber, moveNumberListOnBoard, null);
    }

    public BoardData(Stone[] stonesOnBoard, int[] lastMove, Stone lastMoveColor, boolean blackToPlay, Zobrist zobrist, int moveNumber, int[] moveNumberListOnBoard) {
        this(ImmutablePair.of(19, 19), stonesOnBoard, lastMove, lastMoveColor, blackToPlay, zobrist, moveNumber, moveNumberListOnBoard, null);
    }

    public ImmutablePair<Integer, Integer> getBoardSize() {
        return boardSize;
    }

    public Stone[] getStonesOnBoard() {
        return stonesOnBoard;
    }

    public void setStonesOnBoard(Stone[] stonesOnBoard) {
        this.stonesOnBoard = stonesOnBoard;
    }

    public int[] getLastMove() {
        return lastMove;
    }

    public void setLastMove(int[] lastMove) {
        this.lastMove = lastMove;
    }

    public Stone getLastMoveColor() {
        return lastMoveColor;
    }

    public void setLastMoveColor(Stone lastMoveColor) {
        this.lastMoveColor = lastMoveColor;
    }

    public boolean isBlackToPlay() {
        return blackToPlay;
    }

    public void setBlackToPlay(boolean blackToPlay) {
        this.blackToPlay = blackToPlay;
    }

    public Zobrist getZobrist() {
        return zobrist;
    }

    public void setZobrist(Zobrist zobrist) {
        this.zobrist = zobrist;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int[] getMoveNumberListOnBoard() {
        return moveNumberListOnBoard;
    }

    public void setMoveNumberListOnBoard(int[] moveNumberListOnBoard) {
        this.moveNumberListOnBoard = moveNumberListOnBoard;
    }

    public List<VariationData> getVariationDataList() {
        return variationDataList;
    }

    public void setVariationDataList(List<VariationData> variationDataList) {
        this.variationDataList = variationDataList;
    }

    public Optional<VariationData> getFirstVariation() {
        if (CollectionUtils.isEmpty(variationDataList)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(variationDataList.get(0));
        }
    }

    public double getBlackWinrate() {
        Optional<VariationData> variationData = getFirstVariation();
        double winrate = variationData.map(VariationData::getWinrate).orElse(50.0);
        if (blackToPlay) {
            return winrate;
        } else {
            return 100.0 - winrate;
        }
    }

    public double getWhiteWinrate() {
        return 100.0 - getBlackWinrate();
    }

    public int getCalculationCount() {
        Optional<VariationData> variationData = getFirstVariation();
        return variationData.map(VariationData::getPlayouts).orElse(0);
    }

    public int getTotalCalculationCount() {
        return CollectionUtils.isEmpty(variationDataList)
                ? 0
                : variationDataList.stream().mapToInt(VariationData::getPlayouts).sum();
    }

    public Optional<Boolean> isBlack() {
        switch (lastMoveColor) {
            case BLACK:
                return Optional.of(true);
            case WHITE:
                return Optional.of(false);
            default:
                return Optional.empty();
        }
    }

    public boolean isPass() {
        return lastMove == null || lastMove[0] < 0 || lastMove[0] >= boardSize.getLeft() || lastMove[1] < 0 || lastMove[1] >= boardSize.getRight();
    }

    public void tryUpdateVariationInfo(List<MoveData> newMoveDataList) {
        if (CollectionUtils.isEmpty(newMoveDataList)) {
            return;
        }
        if (CollectionUtils.isEmpty(variationDataList)) {
            variationDataList = newMoveDataList.stream().map(VariationData::new).collect(Collectors.toList());
        } else {
            int totalCalculation = variationDataList.stream().mapToInt(VariationData::getPlayouts).sum();
            int newTotalCalculation = newMoveDataList.stream().mapToInt(MoveData::getPlayouts).sum();
            if (newTotalCalculation > totalCalculation) {
                variationDataList = newMoveDataList.stream().map(VariationData::new).collect(Collectors.toList());
            }
        }
    }

    public int coordsToIndex(int row, int col) {
        return row * boardSize.getLeft() + col;
    }

    public Stone getStoneOnBoard(int row, int col) {
        int index = coordsToIndex(row, col);
        try {
            return stonesOnBoard[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getMoveNumberOnBoard(int row, int col) {
        int index = coordsToIndex(row, col);
        try {
            return moveNumberListOnBoard[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
