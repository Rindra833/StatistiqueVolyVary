let ordreCroissant=true;
        function trierTableau(colonne,numerique=false,fleche){
            const tbody= document.querySelector('.dt tbody');
            const icon=fleche.querySelector('.fleche-tri');
            const tr= Array.from(tbody.querySelectorAll('tr'));
            const tri= tr.sort((d,e)=>{
                const valeurA= d.children[colonne].textContent.trim();
                const valeurB= e.children[colonne].textContent.trim();
                if(numerique){
                    const c=parseFloat(valeurA) - parseFloat(valeurB);
                    if(ordreCroissant){
                        return c;
                    }
                    else{
                        return -c;
                    }
                }
                const text=valeurA.localeCompare(valeurB)
                if(ordreCroissant){
                    return text;
                }
                else{
                    return -text;
                }
            });
            tri.forEach(d => tbody.appendChild(d));
            if (ordreCroissant) {
                icon.textContent = '▲';
            } else {
                icon.textContent = '▼';
            }
            ordreCroissant= !ordreCroissant;
        }