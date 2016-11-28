#pragma once
#include <iostream>
#include <fstream>
#include <utility>
#include <vector>
#include <memory>
#include <queue>
#include <map>

typedef std::pair<int, int> Pos_t;

enum class Cell
{
    EMPTY, WALL
};

template <std::size_t NROWS, std::size_t NCOLS>
class Board
{
public:
    typedef Board<NROWS, NCOLS> My_t;
    Board(const char *filename) :m_data{ }, m_distFromStart(0)
    {
        std::ifstream fin(filename);
        for (std::size_t row = 0; row < NROWS; ++row) {
            for (std::size_t col = 0; col < NCOLS; ++col) {
                char temp;
                do {
                    fin >> temp;
                } while (temp == '\n' || temp == ' ');

                if (temp == '#')
                    m_data[row][col] = Cell::WALL;
                else {
                    m_data[row][col] = Cell::EMPTY;
                    if (temp == 'A')
                        m_currPos = std::make_pair(row, col);
                    else if (temp == 'B') 
                        m_goalPos = std::make_pair(row, col);
                }
            }
        }
        m_hash = m_currPos.first * NCOL + m_currPos.second;
    }
    Board(const My_t&) = default;
    My_t& operator=(My_t&) = default;
    operator int() const
    {
        return m_hash;
    }

    std::size_t Manhattan() const noexcept
    {
        return std::abs(m_goalPos.first - m_currPos.first) + 
            std::abs(m_goalPos.second - m_currPos.second);
    }
    std::size_t GetDistFromStart() const noexcept
    {
        return m_distFromStart;
    }
    std::size_t GetHash() const noexcept
    {
        return m_hash;
    }
    int GetCurrRow() const noexcept
    {
        return m_currPos.first;
    }
    int GetCurrCol() const noexcept
    {
        return m_currPos.second;
    }
    std::vector<std::shared_ptr<My_t>> Children;
    std::weak_ptr<My_t> Parent;

    bool CanIncrementRow() const noexcept
    {
        return m_currPos.first < NROWS - 1 && m_data[m_currPos.first + 1][m_currPos.second] != Cell::WALL;
    }
    void IncrementRow() noexcept
    {
        m_currPos.first++;
        m_distFromStart++;
        m_hash = m_currPos.first * NCOL + m_currPos.second;
    }
    bool CanIncrementCol() const noexcept
    {
        return m_currPos.second < NCOLS - 1 && m_data[m_currPos.first][m_currPos.second + 1] != Cell::WALL;
    }
    void IncrementCol() noexcept
    {
        m_currPos.second++;
        m_distFromStart++;
        m_hash = m_currPos.first * NCOL + m_currPos.second;
    }
    bool CanDecrementRow() const noexcept
    {
        return m_currPos.first > 0 && m_data[m_currPos.first - 1][m_currPos.second] != Cell::WALL;
    }
    void DecrementRow() noexcept
    {
        m_currPos.first--;
        m_distFromStart++;
        m_hash = m_currPos.first * NCOL + m_currPos.second;
    }
    bool CanDecrementCol() const noexcept
    {
        return m_currPos.second > 0 && m_data[m_currPos.first][m_currPos.second - 1] != Cell::WALL;
    }
    void DecrementCol() noexcept
    {
        m_currPos.second--;
        m_distFromStart++;
        m_hash = m_currPos.first * NCOL + m_currPos.second;
    }

private:
    Cell m_data[NROWS][NCOLS];
    Pos_t m_goalPos;
    Pos_t m_currPos;
    std::size_t m_distFromStart;
    std::size_t m_hash;
};


template <class TNode>
void GenerateChildren(std::shared_ptr<TNode> node);


template <std::size_t NROWS, std::size_t NCOLS>
void GenerateChildren<Board<NROWS, NCOLS>>(std::shared_ptr<Board<NROWS, NCOLS>> parent)
{
    using Node_t = Board<NROWS, NCOLS>;
    if (parent->CanDecrementRow()) {
        auto p = std::make_shared<My_t>(*this);
        p->DecrementRow();
        parent->Children.emplace_back(std::move(p));
    }
    if (parent->CanIncrementRow()) {
        auto p = std::make_shared<My_t>(*this);
        p->IncrementRow();
        parent->Children.emplace_back(std::move(p));
    }
    if (parent->CanDecrementCol()) {
        auto p = std::make_shared<My_t>(*this);
        p->DecrementCol();
        parent->Children.emplace_back(std::move(p));
    }
    if (parent->CanIncrementCol()) {
        auto p = std::make_shared<My_t>(*this);
        p->IncrementCol();
        parent->Children.emplace_back(std::move(p));
    }
}


class AStar
{
public:
    typedef Board<7, 20> Node_t;

private:
    std::priority_queue<Node_t> m_open;
    std::priority_queue<Node_t> m_closed;

    std::map<int, Node_t> m_index;
};