#include <thread>
#include <mutex>
#include <iostream>
#include <vector> 
#include <list>

#define NUM_LETTERS 500000

using namespace std;

typedef struct linkedList linkedList;
typedef struct node node;

struct linkedList {
    struct node * head;
};

struct node {
    int val;
    struct node * next;
    bool lock;
};

vector<int> allCards;
struct linkedList l;
void cardParsing()
{
    while (!allCards.empty() && l.head != NULL)
    {
        // first, add a card to the linked list

    }
}

int main()
{
    
    for (int i = 0; i < NUM_LETTERS; i++)
        allCards.push_back(i);
    
    
    thread threads[4];

    for (int i = 0; i < 4; i++)
    {
        threads[i] = std::thread (cardParsing);
    }

    return 0;
}