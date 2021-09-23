// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7;

contract DataSet {

    address private owner;
    string[] data;
    
    /**
     * @dev Set contract deployer as owner
     */
    constructor() {
        owner = msg.sender;
    }

    /**
     * @dev Add a new value
     * @param newData value to store
     */
    function store(string memory newData) public {
        require(msg.sender == owner);
        data.push(newData);
    }

    /**
     * @dev Return value 
     * @return value of the element at index 'index' from data array
     */
    function get(uint index) public view returns (string memory){
        string memory result = data[index];
        return result;
    }
}