'use client'
import { useState } from "react"
import { players, playerInterface } from "../../../data/players";
import Link from "next/link";

export default function PlayersPage(){
  // state to control player search value
  const [searchInput, setSearchInput] = useState('');

  //Making an array of the players that should show up on this search
  const searchedPlayers: playerInterface[] = [];
  for (let p of players.values()) {
    if(p.player_name.toLowerCase().includes(searchInput.toLowerCase())){
      searchedPlayers.push(p);
    }
  }
  
  return(
    <div className="flex flex-col items-center">
      <div>Players</div>
      <input className="border border-black" placeholder="Enter Player's Name" value={searchInput} onChange={(e) => setSearchInput(e.target.value)}/>
      {searchedPlayers.map(player => 
        <Link key={player.id} href={`/players/${player.id}`}>
          <div className="text-gray-700 hover:text-black">
            {player.player_name}
          </div>
        </Link>
      )}
    </div>
  )
}