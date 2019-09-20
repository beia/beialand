using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GenerateWorld : MonoBehaviour
{
    public GameObject grassTile;
    public GameObject rockTile;
    private GameObject[][] tiles;
    private int[][] worldMap;
    private int worldSize;
    // Start is called before the first frame update
    void Start()
    {
        //generate randomized matrix
        worldSize = 120;
        tiles = new GameObject[worldSize][];
        worldMap = new int[worldSize][];
        for (int i = 0; i < tiles.Length; i++)
        {
            tiles[i] = new GameObject[worldSize];
            worldMap[i] = new int[worldSize];
            for (int j = 0; j < tiles[i].Length; j++)
            {
                int rand = Random.Range(1, 101);
                if (rand > 48)
                {
                    worldMap[i][j] = 1;
                    tiles[i][j] = Instantiate(grassTile, new Vector3(j * grassTile.transform.localScale.x, 0, i * grassTile.transform.localScale.z), Quaternion.identity) as GameObject;
                }
                else
                {
                    worldMap[i][j] = 0;
                    tiles[i][j] = Instantiate(rockTile, new Vector3(j * rockTile.transform.localScale.x, 0, i * rockTile.transform.localScale.z), Quaternion.identity) as GameObject;
                }
            }
        }

        //generate world using celular automata
        for (int iterations = 0; iterations < 10; iterations++)
        { 
            //start a new iteration
            for (int i = 1; i < worldMap.Length - 1; i++)
            {
                for (int j = 1; j < worldMap[i].Length - 1; j++)
                {
                    int aliveCells = 0;
                    //check neighbours
                    for (int k = i - 1; k <= i + 1; k++)
                    {
                        for (int l = j - 1; l <= j + 1; l++)
                        {
                            if ((k == i && l == j) == false)
                            {
                                if (worldMap[k][l] == 1)
                                    aliveCells++;
                            }
                        }
                    }

                    //apply neighbours rules
                    if (worldMap[i][j] == 1 && aliveCells < 2)
                        worldMap[i][j] = 0;
                    else if (worldMap[i][j] == 1 && aliveCells >= 4)
                        worldMap[i][j] = 1;
                    else if (worldMap[i][j] == 0 && aliveCells >= 5)
                        worldMap[i][j] = 1;
                    else
                        worldMap[i][j] = 0;
                }   
            }
        }
        
        //show map
        for (int i = 0; i < tiles.Length; i++)
        {
            for (int j = 0; j < tiles[i].Length; j++)
            {
                if(worldMap[i][j] == 0)
                {
                    //rock tile
                    Destroy(tiles[i][j]);
                    tiles[i][j] = Instantiate(rockTile, new Vector3(j * grassTile.transform.localScale.x, 0, i * grassTile.transform.localScale.z), Quaternion.identity) as GameObject;
                }
                if(worldMap[i][j] == 1)
                {
                    //grass tile
                    Destroy(tiles[i][j]);
                    tiles[i][j] = Instantiate(grassTile, new Vector3(j * grassTile.transform.localScale.x, 0,i * grassTile.transform.localScale.z), Quaternion.identity) as GameObject;
                }
            }
        }
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}