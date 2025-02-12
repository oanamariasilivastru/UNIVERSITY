#pragma once
#include <iostream>
#include <mutex>
#include <vector>

class LinkedList
{
private:
    struct Node
    {
        int id;
        int score;
        Node* next;
        Node* previous;
    };

    Node* head;
    Node* tail;
    int size;
    std::vector<int> blackList;
    std::mutex mutex;

public:
    LinkedList();
    ~LinkedList();
    void add(int, int);
    void print(std::ostream&);
    int getSize();
    void remove(int);
    bool tryAddSameId(int, int);
    bool verifyCurrentNode(Node*);
    void addNode(Node*, Node*);
};