'use client'
import { useState } from "react"
import { players, playerInterface } from "../../../data/players";
import Link from "next/link";

export default function Players(){
  // state to control player search value
  const [searchInput, setSearchInput] = useState('');

  //Making an array of the players that should show up on this search
  const searchedPlayers: playerInterface[] = [];

  for (var i in players) {
    if(players[i].name.toLowerCase().includes(searchInput.toLowerCase())){
      console.log(players[i].name);
      searchedPlayers.push(players[i]);
    }
  }
  
  return(
    <>
      <div>Players</div>
      <input className="border border-black" placeholder="Enter Player's Name" value={searchInput} onChange={(e) => setSearchInput(e.target.value)}/>
      {searchedPlayers.map(player => 
        <Link key={player.name} href={`/players/${player.name}`}>
          <div>
            {player.name}
          </div>
        </Link>
      )}
    </>
  )
}