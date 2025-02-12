#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "BST.h"

// Definirea structurii TreeNode
struct TreeNode {
    char key[100];
    int index;
    struct TreeNode* left;
    struct TreeNode* right;
};

// Variabilă globală static pentru index
static int currentIndex = 0;

// Funcția de inițializare a arborelui
struct TreeNode* initializeTree() {
    return NULL;
}

// Crearea unui nou nod în arbore
struct TreeNode* createTreeNode(const char* key) {
    struct TreeNode* newNode = (struct TreeNode*)malloc(sizeof(struct TreeNode));
    strcpy(newNode->key, key);
    newNode->index = currentIndex++;
    newNode->left = NULL;
    newNode->right = NULL;
    return newNode;
}

// Inserarea recursivă a unei chei în arbore
struct TreeNode* insertRecursive(struct TreeNode* root, const char* key) {
    if (root == NULL) {
        return createTreeNode(key);
    }

    if (strcmp(key, root->key) < 0) {
        root->left = insertRecursive(root->left, key);
    } else if (strcmp(key, root->key) > 0) {
        root->right = insertRecursive(root->right, key);
    }

    return root;
}

// Obținerea indexului unei chei în arbore
int getIndexRecursive(struct TreeNode* root, const char* key) {
    if (root == NULL) {
        return -1;
    }

    if (strcmp(key, root->key) == 0) {
        return root->index;
    }

    if (strcmp(key, root->key) < 0) {
        return getIndexRecursive(root->left, key);
    }

    return getIndexRecursive(root->right, key);
}

// Afișarea arborelui în ordine
void inorderRecursivePrint(struct TreeNode* root) {
    if (root != NULL) {
        inorderRecursivePrint(root->left);
        printf("%s : %d\n", root->key, root->index);
        inorderRecursivePrint(root->right);
    }
}
