#ifndef BST_H
#define BST_H

struct TreeNode;

// Declarații ale funcțiilor din BST.c
struct TreeNode* initializeTree();
struct TreeNode* createTreeNode(const char* key);
struct TreeNode* insertRecursive(struct TreeNode* root, const char* key);
int getIndexRecursive(struct TreeNode* root, const char* key);
void inorderRecursivePrint(struct TreeNode* root);

#endif // BST_H
